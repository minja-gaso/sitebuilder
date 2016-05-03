package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Template;


public class TemplateParameters
{
	public static Template process(HttpServletRequest request, Template template)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("TEMPLATE_TITLE") != null)
		{
			template.setTitle(parameterMap.get("TEMPLATE_TITLE")[0]);
		}
		if(parameterMap.get("TEMPLATE_HTML") != null)
		{
			template.setHtml(parameterMap.get("TEMPLATE_HTML")[0]);
		}
		
		return template;
	}
}
