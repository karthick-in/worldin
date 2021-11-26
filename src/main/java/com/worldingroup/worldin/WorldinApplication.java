package com.worldingroup.worldin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

@SpringBootApplication
@RestController
public class WorldinApplication { //extends SpringBootServletInitializer {
	
	private static Logger logger = Logger.getLogger(WorldinApplication.class);
	private static String dbConfig = null;

    @Autowired
    private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(WorldinApplication.class, args);
	}	
	 
	 // Required only if need to run on tomcat as WAR file
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(WorldinApplication.class);
//    }    
    
	@RequestMapping("/")
	public String hello()   
	{
		logger.info("Root app requested...");
		return "<h1>Hello from Worldin, we're up and running...</h1>";  
	}
	
	@RequestMapping("/connectBlob")	
	public String connectBlob()
	{
		try {
			logger.info("Connect blob requested...");
			
			String jmxRemote = System.getProperty("com.sun.management.jmxremote"); 
			String jmxRemotePort = System.getProperty("com.sun.management.jmxremote.port");
			String ip = InetAddress.getLoopbackAddress().getHostAddress();
			
			logger.info(jmxRemote);
			logger.info(jmxRemotePort);
			logger.info(ip);
			
			logger.info(environment.getProperty("java.rmi.server.hostname"));
	        logger.info(environment.getProperty("local.server.port"));
	        logger.info(InetAddress.getLocalHost().getHostAddress());
			
			String storage_account = environment.getProperty("azure.storage.account-name");
			logger.info("Authenticating to Storage account = "+storage_account);
			StorageCredentials creds = new StorageCredentialsAccountAndKey(storage_account, environment.getProperty("azure.storage.account-key"));
			CloudStorageAccount strAcc = new CloudStorageAccount(creds, true);
			logger.info("Storage account authenticated");
			CloudBlobClient blobClient = strAcc.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(environment.getProperty("azure.storage.container-name"));
			CloudBlobDirectory fileInDir = container.getDirectoryReference(environment.getProperty("azure.storage.directory-name"));
			CloudAppendBlob appBlob = fileInDir.getAppendBlobReference(environment.getProperty("azure.storage.file-name"));

			if(appBlob.exists())
			{
				logger.info("Blob available");
				return "Blob exists";
			}
			else
			{
				logger.warn("Blob unavailable!");
				return "Blob unavailable";
			}
			
		} catch (Exception e) {
			logger.error("Error occurred while accessing storage account");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed to access storage account";
		}
		
	}
	
	@RequestMapping("/fakeDb")
	public String getStringFromFakeDb()
	{
		if(dbConfig == null)
		{
			logger.info("First time reading from Fake DB...");
			dbConfig = "Fake DB connection string = pov";
		}
		else
		{
			logger.info("  Persisting!!!");
			
		}
		return dbConfig;
	}
	
	@RequestMapping("/readBlobFile")
    public String readBlobFile() {
		try {
			// Read blob using SAS URL
			InputStream inputStream = new URL(environment.getProperty("azure.storage.read-blob")).openStream();
			String ret = StreamUtils.copyToString(
					inputStream,
	                Charset.defaultCharset());
			logger.info("Blob read success");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "Failed to read Blob via SAS URL";
		}
    }

	// Function to demonstrate log4j stderr logging..
	@RequestMapping("/sendError")
	public String sendError()
	{
		try {
			logger.info("sendError invoked...");
			logger.warn("WARNING message!..");
			logger.error("ERROR message!!..");
			throw new Exception("Testing error raised!");
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.toString());
			e.printStackTrace();
			return "Testing error raised!";		
		}
	}


	@RequestMapping("/curProfile")
	public String curProfile()
	{
		try {
			String curProfile = environment.getProperty("spring.profiles.active");
			logger.info("spring.profiles.active = "+ curProfile);
			return curProfile;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.toString());
			e.printStackTrace();
			return "Failed to get!";
		}
	}
	
	
}
