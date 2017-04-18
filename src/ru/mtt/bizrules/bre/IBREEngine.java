package ru.mtt.bizrules.bre;

import java.util.Map;

import javax.script.ScriptEngine;

import javax.script.ScriptEngineFactory;

import ru.mtt.webapi.core.XSmartObject;

public interface IBREEngine {
    
       public final static String _GROOVY = "groovy";
       public final static String _RUBY = "ruby";
       public final static String _PYTHON = "python";
       public final static String _SCALA = "scala";
       public final static int _STATUS = 0;
    
       void loadFacts(XSmartObject[] facts, int sessId);    
       void loadGlobalFacts (XSmartObject[] facts);
       Map applyRulesSet(String  rulesSet, int sessId);        
       void cleanWM();        
       int openSession ();
       void closeSession (int sessId);
       ScriptEngine getScriptEngine();
       ScriptEngine getScriptEngine(int sessId);
       void setScriptEngineFactory(ScriptEngineFactory se);
       String getAlias();
       void load (String pathId);    
       void init();
       
}
