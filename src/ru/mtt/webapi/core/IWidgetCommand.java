package ru.mtt.webapi.core;

import java.util.List;
import java.util.Map;

/**
 * Visual wiget command interface for parametrized presentation view 
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IWidgetCommand {
    
       public final static int _PAST_MIN = 180;
    
       public void execute(Map<String, List<String>> par);
       public String print();
    
    
}
