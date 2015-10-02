package com.flatflatching.flatflatching.helpers;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBuilder {

    private static final String CLIENT_ID = "client_id";
    private final String uuid;
    private String userName;

    public RequestBuilder(final String uuid) {
        this.uuid = uuid;
    }

    /**Builds the request to send to the server to get the auth-code.
     * @return the requestjson
     * @throws JSONException
     */
    public JSONObject getAuthenticateRequest() throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(CLIENT_ID, getUuid());
        requestParams.put("user_name", getUserName());
        return requestParams;
    }

    /**Builds the request to send to the server to get all surveys.
     * @return the requestjson
     * @throws JSONException
     */
    public JSONObject getSurveysRequest() throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(CLIENT_ID, getUuid());
        return requestParams;
    }

    /**Builds the request if the user wants location based data.
     * @param lat latitude of the cellphone
     * @param lon longitude of the cellphone
     * @param radius radius in which surveys shall be searched for
     * @return the requestjson
     * @throws JSONException
     */
    public JSONObject getSurveysRequest(final double lat, final double lon, final int radius) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(CLIENT_ID, getUuid());
        final JSONObject location = new JSONObject();
        location.put("lat", lat);
        location.put("lon", lon);
        location.put("radius", radius);
        requestParams.put("location", location);
        return requestParams;
    }
    /** Builds the request to send to the server to get a specific survey.
     * @param surveyId The id of the survey to get
     * @return the requestjson
     * @throws JSONException
     */
    public JSONObject getSurveyRequest(final String surveyId) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(CLIENT_ID, getUuid());
        requestParams.put("survey_id", surveyId);
        return requestParams;
    }

    /** Builds the request to send answers to the server.
     * @param surveyId The survey the answer was in
     * @param questionNumber The question the answer was in
     * @param answerNumber The id of the answer
     * @param answerText The text of the answer given
     * @param overWrite Flag if all previous answers shall be overwritten
     * @return
     * @throws JSONException
     */
    public JSONObject sendAnswerRequest(final String surveyId, final int questionNumber,
            final int answerNumber, final String answerText, final boolean overWrite)
            throws JSONException {
        final JSONObject requestParams = new JSONObject();

        requestParams.put(CLIENT_ID, getUuid());
        requestParams.put("survey_id", surveyId);
        requestParams.put("question_number", questionNumber);
        requestParams.put("answer_number", answerNumber);
        requestParams.put("answer_text", answerText);
        requestParams.put("overwrite_answers", overWrite);
        return requestParams;
    }

    private String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    private String getUuid() {
        return uuid;
    }
}
