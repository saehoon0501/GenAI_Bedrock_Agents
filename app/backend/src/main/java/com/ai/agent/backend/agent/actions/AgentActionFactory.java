package com.ai.agent.backend.agent.actions;

import org.springframework.stereotype.Component;

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

    @SuppressWarnings("unchecked")
    public <T> AgentAction<T> createAction(ActionGroup actionGroup) {
        switch (actionGroup) {
            case WEB_SEARCH:                
                return (AgentAction<T>) applicationContext.getBean(GoogleSearch.class);
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
