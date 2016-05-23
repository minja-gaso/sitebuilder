package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page.Component;
import org.sw.marketing.data.website.Data.Website.Template;


public class ComponentParameters
{
	public static Component process(HttpServletRequest request, Component component)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("COMPONENT_TYPE") != null)
		{
			component.setType(parameterMap.get("COMPONENT_TYPE")[0]);
		}
		if(parameterMap.get("COMPONENT_TYPE_VALUE") != null)
		{
			component.setTypeValue(parameterMap.get("COMPONENT_TYPE_VALUE")[0]);
		}
		if(parameterMap.get("COMPONENT_TITLE") != null)
		{
			component.setTitle(parameterMap.get("COMPONENT_TITLE")[0]);
		}
		if(parameterMap.get("COMPONENT_VALUE") != null)
		{
			component.setValue(parameterMap.get("COMPONENT_VALUE")[0]);
		}
		if(parameterMap.get("COMPONENT_STYLE") != null)
		{
			component.setStyle(parameterMap.get("COMPONENT_STYLE")[0]);
		}
		if(parameterMap.get("COMPONENT_ITEM_POSSIBLE") != null)
		{
			component.setItemPossible(Boolean.parseBoolean(parameterMap.get("COMPONENT_ITEM_POSSIBLE")[0]));
		}
		
		return component;
	}
}
