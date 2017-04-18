package ru.mtt.bizrules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.ResourceBundle;
import java.util.Set;
import org.apache.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.mtt.webapi.bizrules.CValidator;
import ru.mtt.webapi.core.IChainProcedure;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.utils.XUtils;



/**
 * Parameters validation controller. 
 * 
 * @author rnasibullin@mtt.ru  Chief 
 */

public class RCNGValidator extends CValidator  {


    @Override
    public Object execute(String string, String[] string2, Object object) {
        return super.execute(string, string2, object);
    }

}
