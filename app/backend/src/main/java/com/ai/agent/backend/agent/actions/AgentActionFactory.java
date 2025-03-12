package com.ai.agent.backend.agent.actions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import com.ai.agent.backend.constant.enums.ActionGroup;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.ai.agent.backend.agent.actions.web.GoogleSearch;

@Component
public class AgentActionFactory implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AgentAction createAction(ActionGroup actionGroup) {
        switch (actionGroup) {
            case WEB_SEARCH:
                return applicationContext.getBean(GoogleSearch.class);
            case WRITER:
                return null;
            default:
                throw new IllegalArgumentException("Invalid action group: " + actionGroup);
        }
    }

    // private void saveFile(String fileKey, MultipartFile file) {
    //     try {
    //         PutObjectRequest request = PutObjectRequest.builder()
    //                 .bucket(bucketName)
    //                 .key(fileKey)
    //                 .build();

    //         s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

    //     } catch (IOException e){
    //         throw new RuntimeException("Error uploading file to S3", e);
    //     }
    // }
}
