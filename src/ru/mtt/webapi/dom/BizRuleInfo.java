package ru.mtt.webapi.dom;

import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XSmartObject;

public class BizRuleInfo  extends XSmartObject  {
    
    String id = null;
    String body = null;

    public BizRuleInfo(String id, String body) {
        super();
        this.id = id;
        this.body = body;
    }

    public BizRuleInfo() {
        super();
    }


    @Override
    public Object getFieldByName(String nm0) throws WAPIException {
       
           String nm = nm0.toLowerCase();
        
           if ("id".equals(nm)) {
               return id;   
           } else if ("body".equals(nm))  {
               return  body;
           }
        
           return null;
           
    }

    @Override
    public void setFieldByName(String nm0, Object v) throws WAPIException {

        String nm = nm0.toLowerCase();
        
        if ("id".equals(nm)) {
            id = (String) v;   
        } else if ("body".equals(nm))  {
            body = (String) v;
        }

    }


}
