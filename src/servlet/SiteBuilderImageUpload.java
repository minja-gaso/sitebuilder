package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.sw.marketing.dao.sitebuilder.DAOFactory;
import org.sw.marketing.dao.sitebuilder.WebsiteDAO;
import org.sw.marketing.data.website.Data;
import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.File;
import org.sw.marketing.data.website.Message;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/upload")
public class SiteBuilderImageUpload extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramWebsiteId = request.getParameter("WEBSITE_ID");
		long websiteID = 0;
		try
		{
			websiteID = Long.parseLong(paramWebsiteId);
		}
		catch (NumberFormatException e)
		{
			websiteID = 0;
		}

		WebsiteDAO websiteDAO = DAOFactory.getWebsiteDAO();
		Website website = websiteDAO.getWebsite(websiteID);
		
		if(website != null)
		{
			Data data = new Data();
			data.getWebsite().add(website);

			/*
			 * generate output
			 */
			TransformerHelper transformerHelper = new TransformerHelper();
			transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("xslUrl"));
			
			String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.website", data);
			String xslScreen = getServletContext().getInitParameter("xslPath") + "file_upload_iframe.xsl";
			String xslStr = ReadFile.getSkin(xslScreen);
			String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));

			System.out.println(xmlStr);
			response.getWriter().println(htmlStr);
		}
		else
		{
			response.getWriter().println("Unknown WEBSITE_ID");
			return;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		java.util.List<String> validFileTypes = new java.util.ArrayList<String>();
//		validFileTypes.add("image/png");
//		validFileTypes.add("image/jpeg");
//		validFileTypes.add("image/gif");
//		validFileTypes.add("image/bmp");
//		
//		String paramWebsiteID = request.getParameter("WEBSITE_ID");
//		long websiteID = 0;
//		try
//		{
//			websiteID = Long.parseLong(paramWebsiteID);
//		}
//		catch (NumberFormatException e)
//		{
//			websiteID = 0;
//		}
//
//		
//		File file = new File();
//		
//		try
//		{
//			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
//			if(items != null)
//			{
//				for (FileItem item : items)
//				{
//					if (item.isFormField())
//					{
//						/*
//						 * process standard form fields
//						 */
//						String fieldName = item.getFieldName();
//						String fieldValue = item.getString();
//						
//						if(fieldName.equals("EVENT_IMAGE_DESCRIPTION"))
//						{
//							file.setDescription(fieldValue);
//						}
//					}
//					else
//					{
//						/*
//						 * process file form field
//						 */
//						String fieldName = item.getFieldName();
//						String fileName = FilenameUtils.getName(item.getName());
//						String fileType = getServletContext().getMimeType(fileName);
//						if(validFileTypes.contains(fileType))
//						{
//							file.setName(fileName);
//							InputStream fileContent = item.getInputStream();
//							String uploadPath = getServletContext().getInitParameter("calFileUploadPath");						
//
//							String calendarUploadPath = uploadPath + request.getParameter("CALENDAR_ID");
//							java.io.File calendarUploadPathFile = new java.io.File(calendarUploadPath);
//							if(!calendarUploadPathFile.exists())
//							{
//								calendarUploadPathFile.mkdir();
//							}
//							
//							String eventUploadPath = calendarUploadPath + java.io.File.separator + request.getParameter("EVENT_ID");
//							java.io.File eventUploadPathFile = new java.io.File(eventUploadPath);
//							if(!eventUploadPathFile.exists())
//							{
//								eventUploadPathFile.mkdir();
//							}
//							
//							String fileUploadPath = eventUploadPath + java.io.File.separator + event.getFileName();
//							java.io.File fileSave = new java.io.File(fileUploadPath);
//							try
//							{
//								item.write(fileSave);
//							}
//							catch (Exception e)
//							{
//								e.printStackTrace();
//							}
//							
//							Message message = new Message();
//							message.setType("success");
//							message.setLabel("The event image has been successfully uploaded.");
//							request.getSession().setAttribute("message", message);
//						}
//						else
//						{
//							Message message = new Message();
//							message.setType("error");
//							message.setLabel("The file extension is not supported.  Please upload only a .gif, .jpg, .jpeg, and .png file.");
//							request.getSession().setAttribute("message", message);
//						}
//					}
//				}
//				eventDAO.updateCalendarEvent(event);
//			}
//		}
//		catch (FileUploadException e)
//		{
//			throw new ServletException("Cannot parse multipart request.", e);
//		}
//
//		process(request, response);
	}
}
