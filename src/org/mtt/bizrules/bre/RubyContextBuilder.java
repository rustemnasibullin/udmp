package org.mtt.bizrules.bre;

public class RubyContextBuilder  extends XBREngine {
    
    @Override
    public String getAlias() {
           return _RUBY;
    }

    String[] paths =  new String[] {getAlias()+"://ru.mtt.icloud.bizrules"};
    
    
    @Override
    public String[] getResources() {
           return paths;
    }
    
    public RubyContextBuilder() {
        super();
    }
    
}
