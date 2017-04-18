package ru.mtt.webapi.xresource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.mtt.webapi.core.WAPIException;

public interface IXResource {
    
       String getResourceByPath (String path);
       Map<String,String> getResourceByPathAsMAP (String path);
       List<String> getResourceByPathAsList (String path);
       void updateResourceByPath (String path, String data) throws WAPIException;
       List<String> getFolderByPathAsList(String path);
       Map<String, String> getFolderByPathAsMAP(String path);
    
}
