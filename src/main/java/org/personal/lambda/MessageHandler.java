package org.personal.lambda;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.personal.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MessageHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private ObjectMapper mapper = new ObjectMapper();
    User user = null;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("MessageHandler Lambda function is invoked:" + requestEvent.toString());
        long start_time = System.currentTimeMillis();
        String requestBody = requestEvent.getBody();

        try {
            user = mapper.readValue(requestBody, User.class);
            long end_time = System.currentTimeMillis();

            logger.log("Time taken for API interaction : " + (start_time-end_time) + " ms");

            Map<String, MessageAttributeValue> messageAttributeValueMap
                    = buildMessageAttribute();

            long sqs_start_time = System.currentTimeMillis();
            //calling the sqs API
            AmazonSQS sqsClient = buildSQSConnection(3, 60000, 5000,
                    LambdaConstants.getConfigMap().getAwsRegion());
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(LambdaConstants.getConfigMap().getSqsUrl())
                    .withMessageBody(user.toString())
                    .withMessageAttributes(messageAttributeValueMap)
                    .withDelaySeconds(5);
            sqsClient.sendMessage(send_msg_request);
            long sqs_end_time = System.currentTimeMillis();
            logger.log("Time taken for sqs interaction : " + (sqs_end_time-sqs_start_time) + " ms");


        } catch (Exception e) {
            logger.log("Exception occurred in lambda : " + context.getFunctionName());
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Powered-By", "AWS Lambda & Serverless");
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent()
                .withHeaders(headers)
                .withBody(user.toString())
                .withStatusCode(200);
    }

    public static AmazonSQS buildSQSConnection(int retryCount, int socketTimeout, int connectionTimeout, String region) {
        ClientConfiguration clientConfig = new ClientConfiguration();

        clientConfig.setSocketTimeout(socketTimeout);
        clientConfig.setConnectionTimeout(connectionTimeout);
        clientConfig.setMaxErrorRetry(retryCount);

        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(region).withClientConfiguration(clientConfig)
                .build();
        return sqsClient;
    }

    /**
     * Generating Message ID
     * @return String
     */
    private static String generateMessageID() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    /**
     * Creating message Attribute
     *
     * @return Map<String, MessageAttributeValue>
     */
    public static Map<String, MessageAttributeValue> buildMessageAttribute() {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
        messageAttributeValue.setStringValue("DemoMessage");
        messageAttributeValue.setDataType("String");
        messageAttributes.put("MessageType", messageAttributeValue);
        messageAttributes.put("message_id", new MessageAttributeValue()
                .withStringValue(generateMessageID())
                .withDataType("String"));
        return messageAttributes;
    }
}


