package com.cloudmine.coderunner;


public interface SnippetContainer {
	public String getSnippetName();
    public Object runSnippet(SnippetArguments arguments);
}
