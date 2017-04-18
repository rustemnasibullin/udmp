package org.mtt.bizrules.bre;

public class PythonContextBuilder   extends XBREngine {
    
    @Override
    public String getAlias() {
           return _PYTHON;
    }

    String[] paths =  new String[] {getAlias()+"://ru.mtt.icloud.bizrules"};
    
    @Override
    public String[] getResources() {
           return paths;
    }
    
    public PythonContextBuilder() {
           super();
    }
    
}
