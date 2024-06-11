package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cli.command.CLICommand;
import servent.message.failProof.BackUpFoundTellMessage;
import servent.message.*;
import servent.message.quorumMessages.QuorumRequestMessage;
import servent.message.removeFile.RemoveFileFromCreatorMessage;
import servent.message.removeFile.RemoveFileMessage;
import servent.message.util.MessageUtil;
import servent.model.ChordFile;
import servent.message.failProof.SearchForBackUpMessage;

/**
 * This class implements all the logic required for Chord to function.
 * It has a static method <code>chordHash</code> which will calculate our chord ids.
 * It also has a static attribute <code>CHORD_SIZE</code> that tells us what the maximum
 * key is in our system.
 * 
 * Other public attributes and methods:
 * <ul>
 *   <li><code>chordLevel</code> - log_2(CHORD_SIZE) - size of <code>successorTable</code></li>
 *   <li><code>successorTable</code> - a map of shortcuts in the system.</li>
 *   <li><code>predecessorInfo</code> - who is our predecessor.</li>
 *   <li><code>valueMap</code> - DHT values stored on this node.</li>
 *   <li><code>init()</code> - should be invoked when we get the WELCOME message.</li>
 *   <li><code>isCollision(int chordId)</code> - checks if a servent with that Chord ID is already active.</li>
 *   <li><code>isKeyMine(int key)</code> - checks if we have a key locally.</li>
 *   <li><code>getNextNodeForKey(int key)</code> - if next node has this key, then return it, otherwise returns the nearest predecessor for this key from my successor table.</li>
 *   <li><code>addNodes(List<ServentInfo> nodes)</code> - updates the successor table.</li>
 *   <li><code>putValue(int key, int value)</code> - stores the value locally or sends it on further in the system.</li>
 *   <li><code>getValue(int key)</code> - gets the value locally, or sends a message to get it from somewhere else.</li>
 * </ul>
 * @author bmilojkovic
 *
 */
public class ChordState {

	public static int CHORD_SIZE;
	public static int chordHash(int value) {
		return 61 * value % CHORD_SIZE;
	}
	private AtomicBoolean systemInConstruction = new AtomicBoolean(false);
	private int chordLevel; //log_2(CHORD_SIZE)
	private ServentInfo[] successorTable;
	private ServentInfo predecessorInfo;
	
	//we DO NOT use this to send messages, but only to construct the successor table
	private List<ServentInfo> allNodeInfo;
	private List<Integer> allNodeIdHistory;
	private List<ServentInfo> quorum;
	private Map<Integer, Object> valueMap;

	private Map<Integer, Object> myFiles;

	private Map<Integer, Boolean> quorumResponses;

	private Map<Integer, Map<Integer, Object>> backups;
	//kada je true znaci da je prosla provera za kvorum, nakon izvrsavanja CS postavlja se na false do prolaska sledece provere
	private AtomicBoolean checkCleared;

	private final Integer lockKey = -8;

	private static List<ServentInfo> friendList;

	private CLICommand commandInProgress = null;
	private String commandArgs = null;

	public ChordState() {
		this.chordLevel = 1;
		int tmp = CHORD_SIZE;
		while (tmp != 2) {
			if (tmp % 2 != 0) { //not a power of 2
				throw new NumberFormatException();
			}
			tmp /= 2;
			this.chordLevel++;
		}
		
		successorTable = new ServentInfo[chordLevel];
		for (int i = 0; i < chordLevel; i++) {
			successorTable[i] = null;
		}
		
		predecessorInfo = null;
		valueMap = new HashMap<>();
		allNodeInfo = new ArrayList<>();
		quorum = new ArrayList<>();

		quorumResponses = new ConcurrentHashMap<>();
		checkCleared = new AtomicBoolean(false);
		friendList = new CopyOnWriteArrayList<>();

		myFiles = new HashMap<>();
		backups = new ConcurrentHashMap<>();
		allNodeIdHistory = new CopyOnWriteArrayList<>();
	}
	
	/**
	 * This should be called once after we get <code>WELCOME</code> message.
	 * It sets up our initial value map and our first successor so we can send <code>UPDATE</code>.
	 * It also lets bootstrap know that we did not collide.
	 */
	public void init(WelcomeMessage welcomeMsg) {
		//set a temporary pointer to next node, for sending of update message
		successorTable[0] = new ServentInfo("localhost", welcomeMsg.getSenderPort());
		this.valueMap = welcomeMsg.getValues();
		//dodaj u valueMap vrednost za proveru locka, key uvek -8

		//0 ako je otkljucano, 1 ako je zakljucano
		this.valueMap.put(lockKey, 0);
		
		//tell bootstrap this node is not a collider
		try {
			Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			
			bsWriter.flush();
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public AtomicBoolean isCheckCleared() {
		return checkCleared;
	}

	public void setCheckCleared(AtomicBoolean checkCleared) {
		this.checkCleared = checkCleared;
	}

	public Map<Integer, Boolean> getQuorumResponses() {
		return quorumResponses;
	}

	public int getChordLevel() {
		return chordLevel;
	}
	
	public ServentInfo[] getSuccessorTable() {
		return successorTable;
	}
	
	public int getNextNodePort() {
		return successorTable[0].getListenerPort();
	}
	
	public ServentInfo getPredecessor() {
		return predecessorInfo;
	}
	
	public void setPredecessor(ServentInfo newNodeInfo) {
		this.predecessorInfo = newNodeInfo;
	}

	public Map<Integer, Object> getValueMap() {
		return valueMap;
	}
	
	public void setValueMap(Map<Integer, Object> valueMap) {
		this.valueMap = valueMap;
	}

	public void setSystemInConstruction(boolean systemInConstruction){
		this.systemInConstruction.set(systemInConstruction);
	}

	public List<Integer> getAllNodeIDsHistory() {
		return allNodeIdHistory;
	}

	public AtomicBoolean getSystemInConstruction() {
		return systemInConstruction;
	}

	public List<ServentInfo> getAllNodeInfo() {
		return allNodeInfo;
	}

	public boolean isCollision(int chordId) {
		if (chordId == AppConfig.myServentInfo.getChordId()) {
			return true;
		}
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() == chordId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if we are the owner of the specified key.
	 */
	public boolean isKeyMine(int key) {
		if (predecessorInfo == null) {
			return true;
		}
		
		int predecessorChordId = predecessorInfo.getChordId();
		int myChordId = AppConfig.myServentInfo.getChordId();
		
		if (predecessorChordId < myChordId) { //no overflow
			if (key <= myChordId && key > predecessorChordId) {
				return true;
			}
		} else { //overflow
			if (key <= myChordId || key > predecessorChordId) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Main chord operation - find the nearest node to hop to to find a specific key.
	 * We have to take a value that is smaller than required to make sure we don't overshoot.
	 * We can only be certain we have found the required node when it is our first next node.
	 */
	public ServentInfo getNextNodeForKey(int key) {
		if (isKeyMine(key)) {
			return AppConfig.myServentInfo;
		}
		
		//normally we start the search from our first successor
		int startInd = 0;
		
		//if the key is smaller than us, and we are not the owner,
		//then all nodes up to CHORD_SIZE will never be the owner,
		//so we start the search from the first item in our table after CHORD_SIZE
		//we know that such a node must exist, because otherwise we would own this key
		if (key < AppConfig.myServentInfo.getChordId()) {
			int skip = 1;
			while (successorTable[skip].getChordId() > successorTable[startInd].getChordId()) {
				startInd++;
				skip++;
			}
		}

		//ignore - irelevant for this
		for(ServentInfo serventInfo : allNodeInfo) {
			if(!allNodeIdHistory.contains(serventInfo.getChordId()))
				allNodeIdHistory.add(serventInfo.getChordId());

		}
		
		int previousId = successorTable[startInd].getChordId();
		
		for (int i = startInd + 1; i < successorTable.length; i++) {
			if (successorTable[i] == null) {
				AppConfig.timestampedErrorPrint("Couldn't find successor for " + key);
				break;
			}
			
			int successorId = successorTable[i].getChordId();
			
			if (successorId >= key) {
				return successorTable[i-1];
			}
			if (key > previousId && successorId < previousId) { //overflow
				return successorTable[i-1];
			}
			previousId = successorId;
		}
		//if we have only one node in all slots in the table, we might get here
		//then we can return any item
		return successorTable[0];
	}

	private void updateSuccessorTable() {
		//first node after me has to be successorTable[0]
		
		int currentNodeIndex = 0;
		ServentInfo currentNode = allNodeInfo.get(currentNodeIndex);
		successorTable[0] = currentNode;
		
		int currentIncrement = 2;
		
		ServentInfo previousNode = AppConfig.myServentInfo;
		
		//i is successorTable index
		for(int i = 1; i < chordLevel; i++, currentIncrement *= 2) {
			//we are looking for the node that has larger chordId than this
			int currentValue = (AppConfig.myServentInfo.getChordId() + currentIncrement) % CHORD_SIZE;
			
			int currentId = currentNode.getChordId();
			int previousId = previousNode.getChordId();
			
			//this loop needs to skip all nodes that have smaller chordId than currentValue
			while (true) {
				if (currentValue > currentId) {
					//before skipping, check for overflow
					if (currentId > previousId || currentValue < previousId) {
						//try same value with the next node
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				} else { //node id is larger
					ServentInfo nextNode = allNodeInfo.get((currentNodeIndex + 1) % allNodeInfo.size());
					int nextNodeId = nextNode.getChordId();
					//check for overflow
					if (nextNodeId < currentId && currentValue <= nextNodeId) {
						//try same value with the next node
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				}
			}
		}

		systemInConstruction.set(false);
	}

	public void executeCommandInProgress() {
		if (commandInProgress != null && commandArgs != null) {
			commandInProgress.execute(commandArgs);
		}
	}

	public void removeNode(int predecessorPort) {
		systemInConstruction.set(true);
		ServentInfo node = null;
		for(ServentInfo si : allNodeInfo){
			if(si.getListenerPort() == predecessorPort){
				node = si;
				break;
			}
		}

		if(node != null){
			allNodeInfo.remove(node);
		}else{
			return;
		}

		allNodeInfo.sort(Comparator.comparingInt(ServentInfo::getChordId));
		if (predecessorInfo.getListenerPort() == predecessorPort) {
			for(int i = 0; i < allNodeInfo.size() -1; i++){
				if(allNodeInfo.get(i).getChordId() == AppConfig.myServentInfo.getChordId()){
					predecessorInfo = allNodeInfo.get(i-1);
					break;
				}
			}
		}

		List<ServentInfo> newList = new ArrayList<>();
		List<ServentInfo> newList2 = new ArrayList<>();

		int myId = AppConfig.myServentInfo.getChordId();
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() < myId) {
				newList2.add(serventInfo);
			} else {
				newList.add(serventInfo);
			}
		}

		allNodeInfo.clear();
		allNodeInfo.addAll(newList);
		allNodeInfo.addAll(newList2);
		if (newList2.size() > 0) {
			predecessorInfo = newList2.get(newList2.size()-1);
		} else {
			predecessorInfo = newList.get(newList.size()-1);
		}

		updateSuccessorTable();
		createQuorum();
	}

	/**
	 * This method constructs an ordered list of all nodes. They are ordered by chordId, starting from this node.
	 * Once the list is created, we invoke <code>updateSuccessorTable()</code> to do the rest of the work.
	 * 
	 */
	public void addNodes(List<ServentInfo> newNodes) {
		systemInConstruction.set(true);
		allNodeInfo.addAll(newNodes);
		
		allNodeInfo.sort(new Comparator<ServentInfo>() {

			@Override
			public int compare(ServentInfo o1, ServentInfo o2) {
				return o1.getChordId() - o2.getChordId();
			}

		});
		
		List<ServentInfo> newList = new ArrayList<>();
		List<ServentInfo> newList2 = new ArrayList<>();
		
		int myId = AppConfig.myServentInfo.getChordId();
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() < myId) {
				newList2.add(serventInfo);
			} else {
				newList.add(serventInfo);
			}
		}
		
		allNodeInfo.clear();
		allNodeInfo.addAll(newList);
		allNodeInfo.addAll(newList2);
		if (newList2.size() > 0) {
			predecessorInfo = newList2.get(newList2.size()-1);
		} else {
			predecessorInfo = newList.get(newList.size()-1);
		}
		
		updateSuccessorTable();
		createQuorum();

	}

	public void addToMyFiles(ChordFile chordFile) {
		if(!myFiles.containsValue(chordFile)){
			myFiles.put(chordFile.getChordId(), chordFile);
		}
	}

	public Map<Integer, Object> getMyFiles() {
		return myFiles;
	}

	private void createQuorum() {
		quorum.clear();
		quorum.add(AppConfig.myServentInfo);

		for (int i = 0; i < successorTable.length; i++) {
			if(quorum.contains(successorTable[i])){
				break;
			}
			quorum.add(successorTable[i]);
		}

		//dodavanje prethnodnika u kvorum
		if (predecessorInfo != null) {
			if(!quorum.contains(predecessorInfo)){
				quorum.add(predecessorInfo);
			}

		}
	}

	public boolean addFriend(Integer chordId){
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() == chordId) {
				if(!friendList.contains(serventInfo)){
					friendList.add(serventInfo);
					AppConfig.timestampedStandardPrint(">>Friend added: " + serventInfo);
					return true;
				}
			}
		}

		return false;

	}

	public boolean isFriend(Integer chordId){
		for (ServentInfo serventInfo : friendList) {
			if (serventInfo.getChordId() == chordId) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean nodeExists(String adressPort){
		String adress = "";
		int port = -1;

		try{
			String[] adressPortSplit = adressPort.split(":");
			adress = adressPortSplit[0];
			port = Integer.parseInt(adressPortSplit[1]);
		}catch (Exception e){
			AppConfig.timestampedErrorPrint("Invalid address:port format.");
			return false;
		}

		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getIpAddress().equals(adress) && serventInfo.getListenerPort() == port) {
				return true;
			}
		}
		return false;
	}

	public int getChordIdForAddressAndPort(String adressPort){
		String adress = "";
		int port = -1;

		try{
			String[] adressPortSplit = adressPort.split(":");
			adress = adressPortSplit[0];
			port = Integer.parseInt(adressPortSplit[1]);
		}catch (Exception e){
			AppConfig.timestampedErrorPrint("Invalid address:port format.");
			return -1;
		}

		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getIpAddress().equals(adress) && serventInfo.getListenerPort() == port) {
				return serventInfo.getChordId();
			}
		}
		return -1;

	}

	public List<ServentInfo> getQuorum() {
		return quorum;
	}

	public Integer getQuorumSize() {
		return quorum.size();
	}

	public void lock(){
		valueMap.put(lockKey, 1);
	}

	public void unlock(){
		valueMap.put(lockKey, 0);
	}

	public synchronized boolean isLocked(){
		return (Integer) valueMap.get(lockKey) == 1;
	}

	public synchronized void setLocked(boolean locked) {
		valueMap.put(lockKey, locked ? 1 : 0);
	}


	public boolean requestCriticalSection(CLICommand command, String args) {
		commandInProgress = command;
		commandArgs = args;
		//AppConfig.timestampedStandardPrint(">>>>>>>>>>>>Quorum size: " + getQuorumSize() + " | quorum: " + quorum);
		quorumResponses.clear();

		lock();
		for (ServentInfo serventInfo : quorum) {
			if (serventInfo.getListenerPort() != AppConfig.myServentInfo.getListenerPort()) {
				QuorumRequestMessage qrm = new QuorumRequestMessage(AppConfig.myServentInfo.getListenerPort(), serventInfo.getListenerPort(), "");
				MessageUtil.sendMessage(qrm);
			}
		}

		long startTime = System.currentTimeMillis();
		long timeout = 5000; // 5 seconds timeout

		while (quorumResponses.size() < quorum.size() - 1) {
			try {
				Thread.sleep(50);
				if (System.currentTimeMillis() - startTime > timeout) {
					AppConfig.timestampedErrorPrint("<<<<<<<<<<<<<Quorum request timed out. Retrying...");
					unlock(); // Unlock before retrying
					Thread.sleep(new Random().nextInt(1000)); // Random backoff
					return requestCriticalSection(command, args); // Retry the request
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				unlock(); // Ensure lock is released on interrupt
				return false;
			}
		}


		quorumResponses.clear();
		//AppConfig.timestampedStandardPrint("==============All responses received, checking...==============");


		if(isCheckCleared().get() && !systemInConstruction.get()){
			checkCleared = new AtomicBoolean(false);
			return true;
		}

		unlock();
		return false;
	}

	public void obtainCriticalSection(CLICommand command, String args) {
		AppConfig.timestampedStandardPrint("--------Requesting critical section...");
		while(!AppConfig.chordState.requestCriticalSection(command, args)){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		AppConfig.timestampedStandardPrint("--------Critical section starting...");;
	}

	public synchronized void releaseCriticalSection() {
		commandInProgress = null;
		commandArgs = null;
		unlock();
	}


	/**
	 * The Chord put operation. Stores locally if key is ours, otherwise sends it on.
	 */
	public void putValue(int key, Object value) {
		if (isKeyMine(key)) {
			valueMap.put(key, value);
			if(valueMap.containsKey(key))
				AppConfig.timestampedStandardPrint("Updated <" + key + "," + value.toString() + "> : " + value.getClass().getName());

			AppConfig.timestampedErrorPrint("Stored <" + key + "," + value.toString() + "> : " + value.getClass().getName());
		} else {
			ServentInfo nextNode = getNextNodeForKey(key);
			PutMessage pm = new PutMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), key, value);
			MessageUtil.sendMessage(pm);
		}
	}

	
	/**
	 * The chord get operation. Gets the value locally if key is ours, otherwise asks someone else to give us the value.
	 * @return <ul>
	 *			<li>The value, if we have it</li>
	 *			<li>-1 if we own the key, but there is nothing there</li>
	 *			<li>-2 if we asked someone else</li>
	 *		   </ul>
	 */
	public Object getValue(int key) {

		if (isKeyMine(key)) {
			if (valueMap.containsKey(key)) {
				return valueMap.get(key);
			} else {
				return -1;
			}
		}
		
		ServentInfo nextNode = getNextNodeForKey(key);
		AskGetMessage agm = new AskGetMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key));
		MessageUtil.sendMessage(agm);
		
		return -2;
	}

	public Object removeValue(int key) {

		//removeValueFromCreator(key);

		if (isKeyMine(key)) {
			if (valueMap.containsKey(key)) {
				AppConfig.timestampedStandardPrint("Removed <" + key + "," + valueMap.get(key) + ">");
				return valueMap.remove(key);
			} else {
				return -1;
			}
		}

		ServentInfo nextNode = getNextNodeForKey(key);
		RemoveFileMessage rfm = new RemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key));
		MessageUtil.sendMessage(rfm);

		return -2;
	}

	public boolean isMyFile(int key) {
		if(myFiles.containsKey(key)){
			return true;
		}
		return false;
	}

	public Object removeValueFromCreator(int key) {

		if (isMyFile(key)) {
			return myFiles.remove(key);
		}

		ServentInfo nextNode = getNextNodeForKey(key);
		RemoveFileFromCreatorMessage rfm = new RemoveFileFromCreatorMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key));
		MessageUtil.sendMessage(rfm);


		return -2;
	}

	public void insertBackUp(Map<Integer, Object> values, int creatorId) {
		backups.put(creatorId, values);
	}

	public void searchForBackup(int key, int whoAsked) {
		if(backups.containsKey(key)){
			AppConfig.timestampedStandardPrint("Backup found for key: " + key);
			ServentInfo nextNode = getNextNodeForKey(key);
			BackUpFoundTellMessage backUpFoundTellMessage = new BackUpFoundTellMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), backups.get(key), String.valueOf(whoAsked), key);
			MessageUtil.sendMessage(backUpFoundTellMessage);
			return;
		}

		//salji dalje
		ServentInfo nextNode = getNextNodeForKey(key);
		SearchForBackUpMessage searchForBackUpMessage = new SearchForBackUpMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key), whoAsked);
		MessageUtil.sendMessage(searchForBackUpMessage);
	}

}
