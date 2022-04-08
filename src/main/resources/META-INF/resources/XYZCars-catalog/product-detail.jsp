<%@ include file="init.jsp" %>

<%
JSONObject catalogItem = (JSONObject) request.getAttribute("catalogItem"); 
String message=(String) request.getAttribute("message"); 

%>

<portlet:actionURL var="actionURL" name="saveAskProductForm">
</portlet:actionURL>

<portlet:renderURL var="renderURL">
</portlet:renderURL>

<div>
	<a class="btn btn-secondary" href="<%=renderURL%>"><liferay-ui:message key="btn-back-to-catalog"></liferay-ui:message></a>
	<div>
		<img class="w-100" src="<%=catalogItem.getString("imageUrl") %>">
		<h1><%= catalogItem.getString("name")%></h1>
		<p><%= catalogItem.getString("description")%></p>
	</div>
	
	<div class="my-2">
		<button class="btn btn-primary" type="button" id="<portlet:namespace />askButton"><liferay-ui:message key="btn-ask"></liferay-ui:message></button>
	</div>
	<aui:form action="<%=actionURL %>" method="POST" cssClass="hide" name="askProductForm">
		<div class="row">
			<div class="col-12 col-md-6">
				<aui:input label="lbl-customer-name" name="customerName" type="text"></aui:input>
			</div>
			<div class="col-12 col-md-6">
				<aui:input label="lbl-customer-last-name" name="customerLastName" type="text"></aui:input>
			</div>
			<div id="questionsContainer" class="col-12">
				<div class="form-group input-text-wrapper">
					<label class="control-label">
						<liferay-ui:message key="lbl-questions"></liferay-ui:message>
					</label>
					<input class="field form-control" type="text" data-question="true"></input>
				</div>
				<div class="form-group input-text-wrapper">
					<aui:input name="customerQuestions" type="hidden"></aui:input>
				</div>
			</div>
			<div class="col-12">
				<aui:input name="productId" type="hidden" value="<%=catalogItem.getLong("id") %>"></aui:input>
				<button class="btn btn-primary" type="button" id="<portlet:namespace />addQuestionButton"><liferay-ui:message key="btn-add-question"></liferay-ui:message></button>
				
				<button class="btn btn-primary" type="button" id="<portlet:namespace />submitAskProductForm"><liferay-ui:message key="btn-send"></liferay-ui:message></button>
			</div>
		</div>
	</aui:form>
</div>

<aui:script use="aui-base,aui-form-validator,aui-modal">
A.on("click",function(element){
	A.one("#<portlet:namespace />askProductForm").toggleClass("hide");
},"#<portlet:namespace />askButton");

A.on("click",function(element){
	A.one("#questionsContainer").append('<div class="form-group input-text-wrapper"><input class="field form-control" type="text" data-question="true"></input></div>');
},"#<portlet:namespace />addQuestionButton");

var askProductFormValidator = new A.FormValidator(
    {
        boundingBox: '#<portlet:namespace />askProductForm',
        rules: {
			<portlet:namespace />customerName: {
			    required: true,
			    alpha: true
			},
			<portlet:namespace />customerLastName: {
			    required: true,
			    alpha: true
			},
			<portlet:namespace />customerQuestions: {
			    required: true
			}
        }
    }
);

A.on("click",function(element){
	var questionFields=A.all('[data-question="true"]');
	var questions=[];
	questionFields.each(function(element){
	    questions.push(element.val());
	});
	A.one("#<portlet:namespace />customerQuestions").val(questions);
	
	askProductFormValidator.validate();
	if(!askProductFormValidator.hasErrors()){
		A.one("#<portlet:namespace />askProductForm").submit();
	}
	
},"#<portlet:namespace />submitAskProductForm");
<%if(message!=null){ %>
	var modal = new A.Modal(
      {
        bodyContent: '<%=message %>',
        centered: true,
        headerContent: 'Modal header',
        render: '#wrapper'
      }
    ).render();
<%} %>
</aui:script>