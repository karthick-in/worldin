package com.worldingroup.worldin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;


import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

@SpringBootApplication
@RestController
public class WorldinApplication {
	
	private static Logger logger = Logger.getLogger(WorldinApplication.class);
	private static String dbConfig = null;
	/*
	 * Load log4j.properties from outside War.
	 */
//	{
//		try {
//			System.out.println("Loading logger...");
//			PropertyConfigurator.configure("src/main/resources/log4j.properties");
//		}catch (Exception e) {
//			System.out.println("ERROR while loading logger!");
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//	}

    @Autowired
    private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(WorldinApplication.class, args);
	}
	
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
			
			StorageCredentials creds = new StorageCredentialsAccountAndKey(environment.getProperty("azure.storage.account-name"), environment.getProperty("azure.storage.account-key"));
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
	
	
}
