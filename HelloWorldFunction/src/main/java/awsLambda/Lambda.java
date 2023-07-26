package awsLambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class Lambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String numeroQueryParameter = input.getQueryStringParameters().get("numero");
        String multiplicadorQueryParameter = input.getQueryStringParameters().get("multiplicador");

        if (numeroQueryParameter == null){
            return response
                    .withBody("{\"error\": \"Parâmetro 'numero' não encontrado\"}")
                    .withStatusCode(400);
        }
        if (multiplicadorQueryParameter == null){
            return response
                    .withBody("{\"error\": \"Parâmetro 'multiplicador' não encontrado\"}")
                    .withStatusCode(400);
        }

        int numero;
        int multiplicador;

        try{
            numero = Integer.parseInt(numeroQueryParameter);
            multiplicador = Integer.parseInt(multiplicadorQueryParameter);
        } catch (NumberFormatException exception){
            return response
                    .withBody("{\"error\": \"Valor de 'numero' ou 'multiplicador' inválido\"}")
                    .withStatusCode(400);
        }

        int resultado = numero * multiplicador;
        String output = String.format("{\n" +
                "\"mensagem\":\"Multiplicação de dois numeros\",\n" +
                "\"code\":\"200\",\n" +
                "\"numero\":\""+numero+"\",\n" +
                "\"multiplicador\":\""+multiplicador+"\",\n" +
                "\"resultado\":\""+resultado+"\"\n" +
                "}");

        return response
                .withStatusCode(200)
                .withBody(output);
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
