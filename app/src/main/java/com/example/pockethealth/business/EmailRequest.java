package com.example.pockethealth.business;

public class EmailRequest {
    private String ColaKey;
    private String tomail;
    private String fromTitle;
    private String subject;
    private String smtpCode;
    private String smtpEmail;
    private String smtpCodeType;
    private boolean isTextContent;
    private String content;

    public EmailRequest(String colaKey, String tomail, String fromTitle, String subject, String smtpCode, String smtpEmail, String smtpCodeType, boolean isTextContent, String content) {
        ColaKey = colaKey;
        this.tomail = tomail;
        this.fromTitle = fromTitle;
        this.subject = subject;
        this.smtpCode = smtpCode;
        this.smtpEmail = smtpEmail;
        this.smtpCodeType = smtpCodeType;
        this.isTextContent = isTextContent;
        this.content = content;
    }

    public EmailRequest() {

    }

    public String getColaKey() {
        return ColaKey;
    }

    public void setColaKey(String colaKey) {
        ColaKey = colaKey;
    }

    public String getTomail() {
        return tomail;
    }

    public void setTomail(String tomail) {
        this.tomail = tomail;
    }

    public String getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(String fromTitle) {
        this.fromTitle = fromTitle;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSmtpCode() {
        return smtpCode;
    }

    public void setSmtpCode(String smtpCode) {
        this.smtpCode = smtpCode;
    }

    public String getSmtpEmail() {
        return smtpEmail;
    }

    public void setSmtpEmail(String smtpEmail) {
        this.smtpEmail = smtpEmail;
    }

    public String getSmtpCodeType() {
        return smtpCodeType;
    }

    public void setSmtpCodeType(String smtpCodeType) {
        this.smtpCodeType = smtpCodeType;
    }

    public boolean isTextContent() {
        return isTextContent;
    }

    public void setTextContent(boolean textContent) {
        isTextContent = textContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
