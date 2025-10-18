package com.maavooripachadi.metrics.dto;


public class ApiQuery {
    private String path; private String from; private String to; private int page = 0; private int size = 200;
    public String getPath() { return path; } public void setPath(String path) { this.path = path; }
    public String getFrom() { return from; } public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; } public void setTo(String to) { this.to = to; }
    public int getPage() { return page; } public void setPage(int page) { this.page = page; }
    public int getSize() { return size; } public void setSize(int size) { this.size = size; }
}