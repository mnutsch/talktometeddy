package com.talkingteddy;

public class ResponseDigest {
	
	
    private String matchingPrompt;
    private Double matchingPromptScore;
    private String actualPrompt;
    
    /**
     * Get matching prompt from NLP server
     * @return
     */
    public String getMatchingPrompt() {
		return matchingPrompt;
	}
    
    /**
     * Set matching prompt for NLP server
     * @param matchingPrompt
     */
	public void setMatchingPrompt(String matchingPrompt) {
		this.matchingPrompt = matchingPrompt;
	}
	
	/**
	 * Get matching prompt score calculated by NLP server
	 * @return
	 */
	public Double getMatchingPromptScore() {
		return matchingPromptScore;
	}
	
	/**
	 * Set matching prompt score calculated by NLP server
	 * @param matchingPromptScore
	 */
	public void setMatchingPromptScore(Double matchingPromptScore) {
		this.matchingPromptScore = matchingPromptScore;
	}
	
	/**
	 * Get actual prompt spoken by the user
	 * @return
	 */
	public String getActualPrompt() {
		return actualPrompt;
	}
	
	/**
	 * Set actual prompt spoken by the user
	 * @param actualPrompt
	 */
	public void setActualPrompt(String actualPrompt) {
		this.actualPrompt = actualPrompt;
	}

}
