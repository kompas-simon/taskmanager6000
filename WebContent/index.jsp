<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>TSKMNGR 6000</title>

<script src="js/required/moment-with-locales.js" type="text/javascript"></script>
<script src="js/required/pikaday.js" type="text/javascript"></script>
<script src="js/required/underscore-min.js" type="text/javascript"></script>
<script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
<script src="js/app.js" type="text/javascript"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js" type="text/javascript"></script>

<link href="css/style.css" type="text/css" rel="stylesheet" />
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
<link href="css/required/pikaday.css" type="text/css" rel="stylesheet" >

<script>
	var taskList;
	var context = "<%=request.getContextPath()%>"; 
	$.ajaxSetup({ scriptCharset: "utf-8" , contentType: "application/json; charset=utf-8"});
	
	$(document).ready(function() {
		$('.filter')
		    .bind('keypress', function(e) {
		    	if(e.keyCode==13){
		    		//enter in filter elements
		    		e.preventDefault();
		    		doFilter();
		    	}
		    });
		$('#filter-button')
			.click(function() {
				doFilter();  
			});
		$('#new_task-button')
		.click(function() {
			addNewTask();  
		});
		$('.sorter')
			.click(function() {
				var id = $(this).attr('id').replace(/sorter-/g, "").replace(/-container/g, "");
				var asc = (!$(this).attr('asc'));
				
				$('.sorter').each(function() {
			        $(this).removeAttr('asc');
			    });
			    if (asc) $(this).attr('asc', 'asc');
			    
			    $('.sorter').removeClass('active-sorter');
			    $(this).addClass('active-sorter');
			    //console.log('SORT: ' + id + ' ' + 'ASC: ' + asc);
			    taskList = doSort(taskList, id, asc);
			});
		$('body').on('click focusin', '.task-item', function(){
			taskItemEnable(this);
			});
		$('.task-type-item')
			.click(function() {	
			    $('.task-type-item').removeClass('task-type-enabled');
			    $(this).addClass('task-type-enabled');
				doFilter();  
			});
		$('body').on('click', '.delete-task', function(){
			var taskId = $(this).parents('.task-item').attr('id').replace(/task-/g, "");
			var taskItem = _.find(taskList,{id:parseInt(taskId)});
			doDelete(taskList, taskItem);
		});
		$('body').on('mouseenter', '.task-save-container', function(){
			var active = document.activeElement;
			if ($(active).hasClass('desc-textarea')) {
				active.blur();
				active.focus();
			}
		});
		$('body').on('click', '.save-task', function(){
			var taskId = $(this).parents('.task-item').attr('id').replace(/task-/g, "");
			var taskItem = _.find(taskList,{id:parseInt(taskId)});
			doUpdate(taskList, taskItem);
			$(this).parents('.task-item').removeClass('changed');
		});
		$('body').on('click', '.cancel-task', function(){
			var id = $(this).parents('.task-item').attr('id').replace(/task-/g, "");
			doRoolbackChanges(taskList, id);
			$(this).parents('.task-item').removeClass('changed');
		});
		$('body').on('change', '.task-checkbox-done', function(){
			var taskId = $(this).parents('.task-item').attr('id').replace(/task-/g, "");
			var taskItem = _.find(taskList,{id:parseInt(taskId)});
			
			if ($(this).prop('checked') === taskItem.done) {
				$(this).parents('.task-item').removeClass('changed');
				$('#task-' + taskId + ' .save-task').attr('disabled', 'disabled');
				$('#task-' + taskId + ' .cancel-task').attr('disabled', 'disabled');
			} else {
				$(this).parents('.task-item').addClass('changed').removeAttr('disabled');
				$('#task-' + taskId + ' .save-task').removeAttr('disabled');
				$('#task-' + taskId + ' .cancel-task').removeAttr('disabled');
			}			
		});
		$('body').on('change', '.desc-textarea', function(){
			var taskId = $(this).parents('.task-item').attr('id').replace(/task-/g, "");
			var taskItem = _.find(taskList,{id:parseInt(taskId)});

			if($(this).val() === taskItem.desc) {
				$(this).parents('.task-item').removeClass('changed');
				$('#task-' + taskId + ' .save-task').attr('disabled', 'disabled');
				$('#task-' + taskId + ' .cancel-task').attr('disabled', 'disabled');
			} else {
				$(this).parents('.task-item').addClass('changed');
				$('#task-' + taskId + ' .save-task').removeAttr('disabled');
				$('#task-' + taskId + ' .cancel-task').removeAttr('disabled');
			}
		});
		$('.date-op-select')
			.change(function() {
				var id = $(this).attr('id').replace(/-select-options/g, "");
				id = '#datepicker-' + id + '-to';
				
				if ($(this).val() === '> <') {
					$(id).removeAttr('disabled');
				} else {
					$(id).attr('disabled', 'disabled');
				}
			});
		$.fn.delegateJSONResult = function(result, append){
			if(append){
				taskList.push(result);
			} else {
				taskList = result;
			}
			//console.log('LIST: ' + JSON.stringify(taskList));
			$('.task-item').remove();
			$('.task-disabled').remove();
			createResults(taskList);
		};
		
		//initial list
		addCountsToLists();
		doFilter();      
	});
</script>
</head>

<body>
<div id="centered-wrapper">
	<div id="header" class="outerContainer">
		<div class="innerContainer">
			<h1>TSKMNGR 6000</h1>
		</div>
	</div>
    <div class="alert alert-danger alert-error alert-dismissible" id="error-container">
        <a href="#" class="close" data-dismiss="alert">&times;</a>
        <strong>Error! </strong><span id="error-text"></span>
    </div>
    <div class="alert alert-info alert-dismissible" id="info-container">
        <a href="#" class="close" data-dismiss="alert">&times;</a>
        <strong>Info: </strong><span id="info-text"></span>
    </div>
	<table id="filter-wrapper"><tbody><tr>
		<td>
			<table id="filter-task_type-container" class="filter innerContainer">
				<tbody><tr>
		            <td><img width="30" height="30" alt="filter icon" src="assets/img/menu55.png"></td>
		        	<td><ul id="filter-task_type-list" >
			                <li class="task-type-item task-type-enabled">
			                	<a href="#">TODO</a>
			                	<span class="badge" id="count-TODO">42</span>
			                </li>
			                <li class="task-type-item">
			                	<a href="#">DONE</a>
			                	<span class="badge" id="count-DONE">42</span>
			                </li>
			                <li class="task-type-item">
			                	<a href="#">ALL</a>
			                	<span class="badge" id="count-ALL">42</span>
			                </li>
			                <li class="task-type-item" id="task-type-TRASH">
			                	<a href="#">TRASH</a>
			                	<span class="badge" id="count-TRASH">42</span>
			                </li>
		             </ul></td>
	             </tr></tbody>
			</table>
		</td>
		
		<%String dateSelectOptions[] = {"<", ">", "=", "> <"};%>
		<td>		
			<table><tbody>
				<tr>
					<td colspan="3">
						<div id="filter-desc-container" class="filter">
							<textarea id="filter-desc" class="form-control custom-control" rows="1" cols="80" placeholder="Description"></textarea>
						</div>
					</td>
				</tr>	
				<tr>
					<td>
						<div id="filter-createDate-container" class="filter">
							<table class="innerContainer"><tbody>
								<tr>
									<td class="date-op-select-container">
										<select class="date-op-select form-control" id="createDate-select-options">
											<%for(int i=0; i < dateSelectOptions.length; i++){
								            %>
							                <option><%=dateSelectOptions[i]%></option>
							             	<%}%>  
										</select>
									</td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" type="text" id="datepicker-createDate-from" placeholder="Create date">
										</div>
									</td>
								</tr>
								<tr><td></td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" disabled type="text" id="datepicker-createDate-to">
										</div>
									</td>
								</tr>
							</tbody></table>
						</div>
					</td>
					<td>
						<div id="filter-dueDate-container" class="filter">
							<table><tbody>
								<tr>
									<td class="date-op-select-container">
										<select class="date-op-select form-control" id="dueDate-select-options">
											<%for(int i=0; i < dateSelectOptions.length; i++){
								            %>
							                <option><%=dateSelectOptions[i]%></option>
							             	<%}%>  
										</select>
									</td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" type="text" id="datepicker-dueDate-from" placeholder="Due date">
										</div>
									</td>
								</tr>
								<tr><td></td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" disabled type="text" id="datepicker-dueDate-to">
										</div>
									</td>
								</tr>
							</tbody></table>
						</div>
					</td>
					<td>
						<div id="filter-resolutionDate-container" class="filter">
							<table><tbody>
								<tr>
									<td class="date-op-select-container">
										<select class="date-op-select form-control" id="resolutionDate-select-options">
											<%for(int i=0; i < dateSelectOptions.length; i++){
								            %>
							                <option><%=dateSelectOptions[i]%></option>
							             	<%}%>  
										</select>
									</td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" type="text" id="datepicker-resolutionDate-from" placeholder="Resolution date">
										</div>
									</td>
								</tr>
								<tr><td></td>
									<td class="picker">
										<div class="inner-addon right-addon">
											<i class="glyphicon glyphicon-calendar"></i>
											<input class="form-control datepicker" disabled type="text" id="datepicker-resolutionDate-to">
										</div>
									</td>
								</tr>
							</tbody></table>
						</div>
					</td>
				</tr>
			</tbody></table>	
		</td>	
		<td>
			<div id="filter-icon-container" class="innerContainer">
				<button type="submit" class="btn btn-default" id="filter-button">
	            	<img width="80" height="80" alt="find it all!" src="assets/img/search100.png">
	            </button>
			</div>
		</td>
	</tr></tbody></table>
	<div id="sorter-wrapper" class="outerContainer">		
		<div id="sorter-gap" class="innerContainer">
		</div>		
		<div id="sorter-createDate-container" class="sorter innerContainer">
			sort create date
		</div>
		<div id="sorter-dueDate-container" class="sorter innerContainer">
			sort due date
		</div>
		<div id="sorter-resolutionDate-container" class="sorter innerContainer">
			sort resolution date
		</div>
		
		<div id="sorter-icon-container" class="innerContainer">
            <img width="100" height="100" alt="sorter icon" src="assets/img/sort52.png">
        </div>
	</div>
	<div id="new-task-container" class="outerContainer">
		<div id="desc-new_task-container" class="innerContainer">
	   		<textarea id="desc-new_task" class="form-control custom-control" rows="2" cols="80" placeholder="Description"></textarea>
		</div>			
		<div id="due_date-new_task-container" class="innerContainer">
			Due date
			<div class="inner-addon right-addon">
				<i class="glyphicon glyphicon-calendar"></i>
				<input class="form-control datepicker" type="text" id="datepicker-due_date-new_task" placeholder="Due date">
			</div>
		</div>			
		<div id="new_task-button-container" class="innerContainer">
			<button type="submit" class="btn btn-default" id="new_task-button">
	        	<img width="80" height="80" alt="Create new task" src="assets/img/add182.png">
	        </button>
		</div>			
	</div>	
	<div id="task-list-container" class="outerContainer">
		<table id="task-list" class="table table-striped table-hover innerContainer">
		</table>
		<div id="loading">
			<img id="loading-img" src="assets/img/ajax-loader.gif" />
		</div>
	</div>

	<div id="footer" class="outerContainer">
		<div class="innerContainer">
			&copy;2015 by <a href="mailto:kompas.simon@gmail.com">Šimon Kompas</a> 
		</div>
	</div>
</div>

    <script type="text/javascript" src="js/calendar.js"></script>
</body>
</html>