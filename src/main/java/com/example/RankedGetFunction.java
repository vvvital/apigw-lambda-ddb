package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.model.User;
import com.example.utils.DDBUtils;
import com.example.utils.RequestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RankedGetFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final DDBUtils ddbUtils = new DDBUtils(System.getenv("TABLE"), System.getenv("REGION"));

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            RequestHelper helper = new RequestHelper(apiGatewayProxyRequestEvent.getBody(),
                    apiGatewayProxyRequestEvent.getPathParameters());
            String userId = helper.getPathParam("id");
            User record = ddbUtils.getRankRecord(userId);

            return response.withStatusCode(200).withBody(RequestHelper.toJson(record));
        } catch (NumberFormatException | JsonProcessingException e) {
            logger.log(e.getMessage());
            return response.withStatusCode(400).withBody("{\"message\": \"Bad userId.\"}");
        }
    }
}
