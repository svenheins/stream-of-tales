package de.svenheins.managers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import de.svenheins.functions.MyUtil;
import de.svenheins.main.GameStates;

public class ServerTextureManager {
	public static ServerTextureManager manager = new ServerTextureManager();
	private HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	
	private HashMap<String, UploadTexture> uploadTexture = new HashMap<String, UploadTexture>();
	
	
	private List<byte[]> downloadTexture = new ArrayList<byte[]>();
	private String downloadTextureName = "";
	private int bytesOfDownloadTexture = 0;
	private int actualDownloadIndex = 0;
	private int numberOfPacketsDownloadTexture = 0;
	private String nameOfPlayerDownloadTexture="";
	
	public void init() {
//		this.map = new HashMap<String, BufferedImage>();
		this.uploadTexture = new HashMap<String, UploadTexture>();
//		uploadTextureName = "";
//		bytesOfUploadTexture = 0;
//		numberOfPacketsUploadTexture=0;
		downloadTexture = new ArrayList<byte[]>();
		downloadTextureName = "";
		bytesOfDownloadTexture = 0;
		actualDownloadIndex = 0;
		numberOfPacketsDownloadTexture = 0;
		nameOfPlayerDownloadTexture="";
	}
	
	public void createPlayerUploadTexture(String playerName) {
		uploadTexture.put(playerName, new UploadTexture());
	}
	
	public void removePlayer(String playerName) {
		uploadTexture.remove(playerName);
	}
	
	public BufferedImage getTexture(String src) {
		if(map.get(src) != null) return map.get(src);
		else {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+src));
				map.put(src, bufferedImage);
				return bufferedImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
	/** get one file by other folderpath than standard */
	public BufferedImage getExternalTexture(String folderPath, String src) {
		if(map.get(src) != null) return map.get(src);
		else {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(new File(folderPath+src));
				map.put(src, bufferedImage);
//				System.out.println("image added: "+src);
				return bufferedImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
	/** first init of external images */
	public void initExternalImages(String folderPath) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(folderPath);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		for (String imageFile: listOfFiles) {
			this.getExternalTexture(folderPath, imageFile);
		}
	}
	
	/** first init of external images */
	public ArrayList<String> listExternalImages(String folderPath) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		File folder = new File(folderPath);
		listOfFiles = MyUtil.listFilesForFolder(folder);
		return listOfFiles;
	}
	
	/** add texture only if there is no texture under the given key */
	public boolean addTexture(String name, BufferedImage image) {
		if (map.containsKey(name)) {
			return false;
		} else
		{
			map.put(name, image);
			return true;
		}
	}
	
	/** overwrite, no matter what is saved at the given key */
	public void overwriteTexture(String name, BufferedImage image) {
		map.put(name, image);
	}
	
	/** return array of texture names */
	public String[] getTextureNames() {
		int lengthOfManager = map.size();
//		System.out.println("length of textureManager: "+ lengthOfManager);
		String[] textureNames = new String[lengthOfManager];
		int index = 0;
		for (String name: map.keySet()) {
			textureNames[index] = name;
			index++;
		}
		return textureNames;
	}
	
	/** return size of manager */
	public int getSize() {
		return map.size();
	}
	
	/** prepare the upload of an existing texture from the map to the client */
	public void prepareTextureForUpload(String playerName, String str) {
		UploadTexture up = this.uploadTexture.get(playerName);
		up.setUploadTextureName(str);
		/** get the BufferedImage */
		BufferedImage bi = map.get(str);//manager.getExternalTexture(GameStates.externalImagesPath, str);
		System.out.println("texture-upload: " +str);
		String fileFormat = "";
		if (str.toLowerCase().endsWith(".jpg")) fileFormat = "jpeg"; 
		else if (str.toLowerCase().endsWith(".png")) fileFormat = "png"; 
		/** convert into byte array */
		byte[] byteImage = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, fileFormat, baos);
			baos.flush();
			byteImage = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/** divide byte array into packets and add each to the ArrayList */
		if (byteImage != null) {
			/** clear uploadTexture-ArrayList */
			up.setUploadTexture(new ArrayList<byte[]>());
//			uploadTexture.put(playerName, new ArrayList<byte[]>());
			up.setBytesOfUploadTexture( byteImage.length );
			int packetCount = (int) (byteImage.length/1024);
			int rest = byteImage.length % 1024;
			if ( rest != 0) {
				packetCount += 1;
			}
			up.setNumberOfPacketsUploadTexture(packetCount);
			/** create byte array that are added to the ArrayList */
			byte[] addPartOfByteImage = null;
			for (int i = 0; i<packetCount ; i++) {
				if (rest!=0 && i == (packetCount-1)) {
					/** last packet with byte-size smaller than 1024 */
					int startIndex = i*1024;
					/** not startIndex+rest-1 because of Arrays.copyOfRange-Declaration */
					int endIndex = startIndex+rest;
					addPartOfByteImage = Arrays.copyOfRange(byteImage, startIndex, endIndex);
				} else {
					/** create a part-array of byteImage-array with size 1024 and iterating start-index */
					int startIndex = i*1024;
					/** not ((i+1)*1024)-1 because of Arrays.copyOfRange-Declaration */
					int endIndex = ((i+1)*1024);
					addPartOfByteImage = Arrays.copyOfRange(byteImage, startIndex, endIndex);
				}
				/** now we can add the byte[] to the ArrayList */
//				ArrayList<byte[]> uploadTextureListForPlayer = uploadTexture.get(playerName);
				up.addPartOfTexture(addPartOfByteImage);
				uploadTexture.put(playerName, up);
			}
		}
		/** everything is well prepared now */
	}
	
	/** get a specific index of the actual uploadTexture */
	public byte[] getTexturePacket(String playerName, int actualIndex) {
		return uploadTexture.get(playerName).getPacket(actualIndex);
	}
	
	/** get length of Texture-Packets */
	public int getLengthOfUploadTexture(String playerName) {
		if (uploadTexture.containsKey(playerName)) {
		
			return uploadTexture.get(playerName).getSize();
		} else {
			return -1;
		}
	}
	
	/** get bytes of upload Texture */
	public int getBytesOfUploadTexture(String playerName) {
		return uploadTexture.get(playerName).getBytesOfUploadTexture();
	}
	
	/** init download */
	public void initDownload(String name, int numberOfPackets, String playerName) {
		downloadTextureName = name;
		actualDownloadIndex = 0;
		numberOfPacketsDownloadTexture = numberOfPackets;
		nameOfPlayerDownloadTexture= playerName;
		
		System.out.println("init download: "+name +" number of packets: "+numberOfPacketsDownloadTexture + " from player "+nameOfPlayerDownloadTexture);
	}
	
	public void resetDownload() {
		downloadTextureName = "";
		actualDownloadIndex = 0;
		numberOfPacketsDownloadTexture = 0;
		nameOfPlayerDownloadTexture= "";
	}
	
	/** get a packet of the actual download texture */
	public void getPartOfDownload(String name, int packetId, byte[] image, String playerName) {
		/** be sure, that you add only one texture at a time */
		if (playerName.equals(nameOfPlayerDownloadTexture)) {
			if (packetId == actualDownloadIndex) {
				/** not last packet */
				if (packetId < numberOfPacketsDownloadTexture -1) {
					bytesOfDownloadTexture = packetId*1024 + image.length;
					/** next index */
					actualDownloadIndex += 1;
					/** finally add the image to byte-Array-List */
					this.downloadTexture.add(image);
				} else {
					/** last packet */
					System.out.println("Got last packet of image "+name+" by player "+playerName);
					bytesOfDownloadTexture = packetId*1024 + image.length;
					/** reset download index */
					actualDownloadIndex = 0;
					/** finally add the image to byte-Array-List */
					this.downloadTexture.add(image);
					/** in the completeDownloadTexture-part we save the texture under a bufferedImage and add it to the manager.map */
					this.completeDownloadTexture();
				}
			} else {
				System.out.println("The packetID "+packetId+" was send. We expected to get the packet-No. "+actualDownloadIndex+"!");
			}
			
		} else
		{
			/** error handling:
			 * either we have a wrong player-upload
			 * or the packetId is wrong
			 *  */
			System.out.println("The player "+playerName+" send the packet, but we expected player "+nameOfPlayerDownloadTexture+" to send his next packet.");
			
		}
	}
	
	/** complete the downloaded texture */
	public void completeDownloadTexture() {
		byte[] completeImageBytes = new byte[bytesOfDownloadTexture];
		/** loop through the ArrayList of the downloadTexture-Object */
		for (int i = 0; i < downloadTexture.size(); i++) {
			for(int j = 0; j < downloadTexture.get(i).length; j++) {
				completeImageBytes[i*1024 + j] = downloadTexture.get(i)[j];
			}
		}
		
		/** now convert the byte[] image to BufferedImage */
        ByteArrayInputStream bis = new ByteArrayInputStream(completeImageBytes);
        String fileFormat = "";
		if (downloadTextureName.toLowerCase().endsWith(".jpg")) fileFormat = "jpeg"; 
		else if (downloadTextureName.toLowerCase().endsWith(".png")) fileFormat = "png"; 
        Iterator<?> readers = ImageIO.getImageReadersByFormatName(fileFormat);
 
        ImageReader reader = (ImageReader) readers.next();
        Object source = bis; // File or InputStream, it seems file is OK
        ImageInputStream iis;
		try {
			iis = ImageIO.createImageInputStream(source);
			reader.setInput(iis, true);
	        ImageReadParam param = reader.getDefaultReadParam();
	        Image image = reader.read(0, param);
	        /** image is ready */
	        BufferedImage bufferedImage = null;
	        if (fileFormat == "jpeg") bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	        else if (fileFormat == "png") bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	        else System.out.println("wrong file format!");
	        
	        if (!fileFormat.equals("")) {
		        /** bufferedImage is drawn */
		        Graphics2D g2 = bufferedImage.createGraphics();
		        g2.drawImage(image, null, null);
		        /** finally add the BufferedImage under the given name */
		        map.put(downloadTextureName, bufferedImage);
		        
		        System.out.println("COMPLETED texture "+downloadTextureName);
		        /** test if we got a good image */
		        File imageFile = new File(GameStates.externalImagesPath+downloadTextureName);
		        ImageIO.write(bufferedImage, fileFormat, imageFile);
		        
		        /** clear all the download variables */
		        downloadTexture = new ArrayList<byte[]>();
		    	downloadTextureName = "";
		    	bytesOfDownloadTexture = 0;
		    	actualDownloadIndex = 0;
		    	numberOfPacketsDownloadTexture = 0;
		    	nameOfPlayerDownloadTexture="";
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/** the download must be reseted */
		this.resetDownload();
	}
	
	
	public boolean contains(String name) {
		return map.containsKey(name);
	}
	
	public int getActualDownloadIndex(){
		return actualDownloadIndex;
	}
	
	public String getDownloadTextureName() {
		return downloadTextureName;
	}
	
	public ArrayList<String> getTextureUploadList(String playerName) {
		return uploadTexture.get(playerName).getTextureUploadList();
	}
	
	public void setTextureUploadList(String playerName, ArrayList<String> list) {
		uploadTexture.get(playerName).setTextureUploadList(list);
	}
	
	public String getUploadTextureName(String playerName) {
		return uploadTexture.get(playerName).getUploadTextureName();
	}
	
	public int prepareNextTextureForUpload(String playerName, String oldTexture) {
		ArrayList<String> list = uploadTexture.get(playerName).getTextureUploadList();
		list.remove(oldTexture);
//		this.textureUploadList.remove(oldTexture);
		if (list.size() > 0) {
			this.prepareTextureForUpload(playerName, list.get(0));
			return list.size();
		} else {
			return 0;
		}
	}
	
	public int getCountTextureForUpload(String playerName) {
		if (uploadTexture.containsKey(playerName)) {
			return uploadTexture.get(playerName).getTextureUploadList().size();
		} else
			return -1;
	}

	public int getNumberOfPacketsUploadTexture(String playerName) {
		return uploadTexture.get(playerName).getNumberOfPacketsUploadTexture();
	}
	
	public boolean containsPlayer(String playerName) {
		return uploadTexture.containsKey(playerName);
				
	}
	
	public boolean hasTextureToUpload(String playerName, String textureToUpload) {
		return uploadTexture.get(playerName).getTextureUploadList().contains(textureToUpload);
	}
}
