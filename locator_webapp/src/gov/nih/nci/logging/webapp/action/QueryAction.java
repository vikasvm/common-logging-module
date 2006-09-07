//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.0.0/xslt/JavaClass.xsl

package gov.nih.nci.logging.webapp.action;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import gov.nih.nci.logging.api.applicationservice.Query;
import gov.nih.nci.logging.api.applicationservice.QueryImpl;
import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.api.applicationservice.exception.QuerySpecificationException;
import gov.nih.nci.logging.api.applicationservice.exception.SearchCriteriaSpecificationException;
import gov.nih.nci.logging.api.logger.util.ApplicationConstants;
import gov.nih.nci.logging.webapp.form.QueryForm;
import gov.nih.nci.logging.webapp.util.Constants;
import gov.nih.nci.logging.webapp.util.StringUtils;
import gov.nih.nci.logging.webapp.viewobjects.SearchResultPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

public class QueryAction extends Action
{

	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{

		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();

		HttpSession session = request.getSession();

		QueryForm queryForm = (QueryForm) form;

		if (session.isNew() || (session.getAttribute(Constants.LOGIN_OBJECT) == null))
		{
			return mapping.findForward(Constants.FORWARD_PUBLIC_LOGIN);
		}

		if (!StringUtils.isBlankOrNull(queryForm.getActivity()))
		{

			queryForm.setActivity(null);
			session.setAttribute(Constants.CURRENT_FORM, queryForm);

			session.removeAttribute(Constants.SEARCH_RESULTS_PAGE);
			session.removeAttribute(Constants.VIEW_PAGE_NUMBER);

			boolean success = false;
			success = performQuery(queryForm, session);
			
			if (success)
			{
				return mapping.findForward(Constants.FORWARD_QUERY_RESULTS);
			}
		}else{
			//set Query Form default Values.
			
			java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(ApplicationConstants.DATE_FORMAT);
			java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat(ApplicationConstants.TIME_FORMAT);
			
			Date currentDate = new Date(System.currentTimeMillis());
			queryForm.setStartDate(dateFormat.format(currentDate));
			queryForm.setStartTime(timeFormat.format(currentDate));
			queryForm.setEndDate(dateFormat.format(currentDate));
			queryForm.setEndTime(timeFormat.format(currentDate));
			queryForm.setRecordCount(new Integer(100).toString());
			queryForm.setActivity(null);
			session.setAttribute(Constants.CURRENT_FORM, queryForm);
			
		}
		return mapping.findForward(Constants.FORWARD_QUERY);

	}

	private boolean performQuery(QueryForm queryForm, HttpSession session)
	{
		try
		{
			Query query = new QueryImpl();
			query.setCriteria(getSearchCriteria(queryForm));

			int totalResultSize = query.totalResultSize();

			if (totalResultSize > 0)
			{
				// Query results
				Collection resultCollection = query.query(Constants.DEFAULT_PAGE_NUMBER, Constants.DEFAULT_PAGE_SIZE);
				List resultList = (List) resultCollection;

				// Set Search Result Page information
				SearchResultPage searchResultPage = new SearchResultPage();
				searchResultPage.setTotalResultSize(totalResultSize);
				searchResultPage.setPageSize(Constants.DEFAULT_PAGE_SIZE);
				searchResultPage.setCurrentPageNumber(Constants.DEFAULT_PAGE_NUMBER);
				searchResultPage.setSearchResultMessage(Constants.RESULTS_MESSAGE);
				searchResultPage.setSearchResultObjects(resultList);

				// Set Search Results
				session.setAttribute(Constants.SEARCH_RESULTS_PAGE, searchResultPage);
				session.setAttribute(Constants.VIEW_PAGE_NUMBER, searchResultPage.getCurrentPageNumber());
				return true;
			}
			else
			{
				// Set Search Result Page information
				SearchResultPage searchResultPage = new SearchResultPage();
				searchResultPage.setTotalResultSize(totalResultSize);
				searchResultPage.setSearchResultMessage(Constants.NO_RESULTS_MESSAGE);

				// Set Search Results
				session.setAttribute(Constants.SEARCH_RESULTS_PAGE, searchResultPage);
				session.setAttribute(Constants.VIEW_PAGE_NUMBER, searchResultPage.getCurrentPageNumber());
				return false;
			}
		}
		catch (QuerySpecificationException e)
		{
			// Exception trying to Query.
			// TODO Show Error to the User
			return false;
		}
		catch (SearchCriteriaSpecificationException e)
		{
			// Exception trying to Query.
			// TODO Show Error to the User
			return false;
		}
	}

	private SearchCriteria getSearchCriteria(QueryForm queryForm)
	{
		SearchCriteria searchCriteria = new SearchCriteria();

		searchCriteria.setApplication(!StringUtils.isBlankOrNull(queryForm.getApplication()) ? queryForm.getApplication() : null);
		searchCriteria.setEndDate(!StringUtils.isBlankOrNull(queryForm.getEndDate()) ? queryForm.getEndDate() : null);
		searchCriteria.setEndTime(!StringUtils.isBlankOrNull(queryForm.getEndTime()) ? queryForm.getEndTime() : null);
		searchCriteria.setLogLevel(!StringUtils.isBlankOrNull(queryForm.getLogLevel()) ? queryForm.getLogLevel() : null);
		searchCriteria.setMessage(!StringUtils.isBlankOrNull(queryForm.getMessage()) ? queryForm.getMessage() : null);
		searchCriteria.setNdc(!StringUtils.isBlankOrNull(queryForm.getNdc()) ? queryForm.getNdc() : null);
		searchCriteria.setObjectID(!StringUtils.isBlankOrNull(queryForm.getObjectID()) ? queryForm.getObjectID() : null);
		searchCriteria.setObjectName(!StringUtils.isBlankOrNull(queryForm.getObjectName()) ? queryForm.getObjectName() : null);
		searchCriteria.setOperation(!StringUtils.isBlankOrNull(queryForm.getOperation()) ? queryForm.getOperation() : null);
		searchCriteria.setOrganization(!StringUtils.isBlankOrNull(queryForm.getOrganization()) ? queryForm.getOrganization() : null);
		searchCriteria.setServer(!StringUtils.isBlankOrNull(queryForm.getServer()) ? queryForm.getServer() : null);
		searchCriteria.setSessionID(!StringUtils.isBlankOrNull(queryForm.getSessionID()) ? queryForm.getSessionID() : null);
		searchCriteria.setStartDate(!StringUtils.isBlankOrNull(queryForm.getStartDate()) ? queryForm.getStartDate() : null);
		searchCriteria.setStartTime(!StringUtils.isBlankOrNull(queryForm.getStartTime()) ? queryForm.getStartTime() : null);
		searchCriteria.setThreadName(!StringUtils.isBlankOrNull(queryForm.getThread()) ? queryForm.getThread() : null);
		searchCriteria.setThrowable(!StringUtils.isBlankOrNull(queryForm.getThrowable()) ? queryForm.getThrowable() : null);
		searchCriteria.setUserName(!StringUtils.isBlankOrNull(queryForm.getUser()) ? queryForm.getUser() : null);

		return searchCriteria;
	}
}