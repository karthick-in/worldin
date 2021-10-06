package com.worldingroup.worldin;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

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

@SpringBootApplication
@RestController
public class WorldinApplication {

    @Autowired
    private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(WorldinApplication.class, args);
	}
	
	@RequestMapping("/")
	public String hello()   
	{  
		return "<h1>Hello from Worldin, we're up and running...</h1>";  
	}
	
	@RequestMapping("/connectBlob")	
	public String connectBlob()
	{
		try {
			
			StorageCredentials creds = new StorageCredentialsAccountAndKey(environment.getProperty("azure.storage.account-name"), environment.getProperty("azure.storage.account-key"));
			CloudStorageAccount strAcc = new CloudStorageAccount(creds, true);
			CloudBlobClient blobClient = strAcc.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(environment.getProperty("azure.storage.container-name"));
			CloudBlobDirectory fileInDir = container.getDirectoryReference(environment.getProperty("azure.storage.directory-name"));
			CloudAppendBlob appBlob = fileInDir.getAppendBlobReference(environment.getProperty("azure.storage.file-name"));
			
			if(appBlob.exists())
			{
				return "Blob exists";
			}
			else
			{
				return "Blob unavailable";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed to access storage account";
		}
		
	}

}
