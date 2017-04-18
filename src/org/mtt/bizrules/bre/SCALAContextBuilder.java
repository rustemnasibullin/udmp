package org.mtt.bizrules.bre;

public class SCALAContextBuilder  extends XBREngine {
    
    @Override
    public String getAlias() {
           return _SCALA;
    }

    String[] paths = new String[] {getAlias()+"://ru.mtt.icloud.bizrules"};
    
    
    @Override
    public String[] getResources() {
           return paths;
    }
    
    public SCALAContextBuilder() {
        super();
    }
}
