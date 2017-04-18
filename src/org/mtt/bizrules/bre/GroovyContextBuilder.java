package org.mtt.bizrules.bre;

import groovy.lang.GroovyClassLoader;

import javax.script.ScriptEngine;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.StaticApplicationContext;

import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.Invocable;

import javax.script.ScriptException;
import org.mtt.webapi.core.WAPIException;
import org.mtt.webapi.core.XSmartObject;
import org.mtt.webapi.dom.SimpleXSmartObject;


public class GroovyContextBuilder extends XBREngine {
    
    @Override
    public String getAlias() {
           return _GROOVY;
    }

    String[] paths = new String[] {getAlias()+"://ru.mtt.icloud.bizrules"};
    
    
    @Override
    public String[] getResources() {
           return paths;
    }
    
    
    

}

