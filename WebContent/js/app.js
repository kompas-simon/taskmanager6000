//sort list by filter options
function doFilter() {
	$("#task-list").hide();
	$("#loading").show();
	
    var parameters = {};    
    parameters['task-type'] = $('.task-type-enabled a').first().html();
    parameters['desc'] = $('#filter-desc').val();  
    
    $('.date-op-select').each(function() {
		var id = $(this).attr('id');
		parameters[id]=$(this).val();
    });
	$('.datepicker').each(function() {
		var id = $(this).attr('id').replace(/datepicker-/g, "");
		parameters[id]=parseDate($(this).val());
	});
	changeDateOrder(parameters, 'createDate');
	changeDateOrder(parameters, 'dueDate');
	changeDateOrder(parameters, 'resolutionDate');
	
	$.getJSON(context + '/getTasks', $.param(parameters), function() {
		})
		  .done(function(json) {
			  $.fn.delegateJSONResult(json, false);
		  })
		  .fail(function(jqXHR, textStatus, errorThrown) {
			  $("#error-text").text('filter tasks failed [' + errorThrown + ']');
			  $("#error-container").show();
		  })
		  .always(function() {
			  checkIfNoResults();
			  $("#loading").hide();
			  $("#task-list").show();
		  });
}

//counts to task type list badge-s
function addCountsToLists() {
	$.getJSON(context + '/getTaskListCount', function() {
	})
	  .done(function(json) {
		  $('.badge').each(function() {
			  var key = $(this).attr('id').replace(/count-/g, "");
			  $(this).text(json[key]);
		  });
	  })
	  .fail(function(jqXHR, textStatus, errorThrown) {
		  $("#error-text").text('add count to task types failed [' + errorThrown + ']');
		  $("#error-container").show();
	  })
	  .always(function() {
	  });
}

//sort list by prop, asc
function doSort(list, prop, asc) {
	$('.task-item').remove();
	list = list.sort(function(a, b) {
        if (asc) return (a[prop] > b[prop])?-1:1;
        else return (b[prop] > a[prop])?-1:1;
	});
	createResults(list);
	return list;
}

//save new task
function addNewTask() {
	var parameters = {};
	parameters['desc'] = $('#desc-new_task').val().trim();
	parameters['dueDate'] = parseDate($('#datepicker-due_date-new_task').val());
	parameters['createDate'] = moment().format('YYYY-MM-DD');

	console.log('XXX: ' + $.param(parameters));
	
	if(!parameters['desc']) {
		$("#error-text").text('Description is mandatory field to add new task');
		$("#error-container").show();
		return;
	}
	
	$.getJSON(context + '/addTask', $.param(parameters), function() {
		})
		  .done(function(newTaskJSON) {
			  doFilter();
			  
			  incrementBadge('TODO');
			  incrementBadge('ALL');
			  
			  $('#desc-new_task').val('');
			  $('#datepicker-due_date-new_task').val('');
		  })
		  .fail(function(jqXHR, textStatus, errorThrown) {
			  $("#error-text").text('add new task failed [' + errorThrown + ']');
			  $("#error-container").show();
		  });
}

//delete task
function doDelete(taskList, taskItem) {
	var parameters = {};
	parameters['id'] = taskItem.id;
	parameters['version'] = taskItem.version;
		
	$.get(context + '/deleteTask', $.param(parameters), function() {
		})
		  .done(function(updatedTask) {			  
			  taskList = deleteObjectFromListByPair(taskList, 'id', taskItem.id);
			  if (updatedTask !== '') {
				  taskList.push(updatedTask);
				  taskList = doSort(taskList, 'id', false);
				  $('.sorter').removeClass('active-sorter');

				  $.fn.delegateJSONResult(taskList, false);
				  //notify user about update
				  taskItemEnable($('#task-' + updatedTask.id));
				  $("#info-text").text('your task has been changed meanwhile. Reload was done. Your task is up-to-date.');
				  $("#info-container").show();
			  } else {	
				  $.fn.delegateJSONResult(taskList, false);
				  
				  incrementBadge('TRASH');
				  decrementBadge('ALL');
				  decrementBadge($('.task-type-enabled a').text());
			  }
		  })
		  .fail(function(jqXHR, textStatus, errorThrown) {
			  //console.log('jqXHR: ' + JSON.stringify(jqXHR) + ' textStatus: ' + textStatus + ' errorThrown:' + errorThrown);
			  $("#error-text").text('delete task failed [' + errorThrown + ']');
			  $("#error-container").show();
		  })
		  .always(function() {
			  checkIfNoResults();
		  });
}

//update task data (desc, done)
function doUpdate(taskList, taskItem) {
	var parameters = {};
	parameters['id'] = taskItem.id;
	parameters['desc'] = $('#task-' + taskItem.id + ' .desc-textarea').val();
	parameters['resolution_date'] = '';
//	parameters['due_date'] = due_date;
	var checked = $('#task-' + taskItem.id + ' .task-checkbox-done').prop('checked');
	parameters['done'] = checked;
	parameters['version'] = taskItem.version;
	
	if (checked && !taskItem.done) {
		parameters['resolution_date'] = moment().format('YYYY-MM-DD');
	}
		
	$.get(context + '/updateTask', $.param(parameters), function() {
		})
		  .done(function(updatedTask) {
			  var enabledTaskType = $('.task-type-enabled a').first().html();

			  taskList = deleteObjectFromListByPair(taskList, 'id', taskItem.id);
			  
			  if (updatedTask !== '') {
				  taskList.push(updatedTask);
				  taskList = doSort(taskList, 'id', false);
				  $('.sorter').removeClass('active-sorter');

				  $.fn.delegateJSONResult(taskList, false);
				  //notify user about update
				  taskItemEnable($('#task-' + updatedTask.id));
				  $("#info-text").text('your task has been changed meanwhile. Reload was done. Your task is up-to-date.');
				  $("#info-container").show();
			  } else {				  
				  if (enabledTaskType === 'ALL' || (checked === taskItem.done)) {
					  taskItem.version++;
					  taskItem.done = checked;
					  taskItem.desc = parameters['desc'];
					  taskList.push(taskItem);
					  taskList = doSort(taskList, 'id', false);
					  $('.sorter').removeClass('active-sorter');
				  }
				  if (checked && !taskItem.done) {
					  incrementBadge('DONE');
					  decrementBadge('TODO');
				  }
				  if (!checked && taskItem.done) {
					  incrementBadge('TODO');
					  decrementBadge('DONE');
				  }		
				  $.fn.delegateJSONResult(taskList, false);		  
			  }			  
		  })
		  .fail(function(jqXHR, textStatus, errorThrown) {
			  //console.log('jqXHR: ' + JSON.stringify(jqXHR) + ' textStatus: ' + textStatus + ' errorThrown:' + errorThrown);
			  $("#error-text").text('update task failed [' + errorThrown + ']');
			  $("#error-container").show();
		  })
		  .always(function() {
			  checkIfNoResults();
		  });
}

//rollback changes made on task item
function doRoolbackChanges(taskList, taskId) {
	var taskItem = _.find(taskList,{id:parseInt(taskId)});
	
	$('#task-' + taskId + ' .task-checkbox-done').prop('checked', taskItem.done);
	$('#task-' + taskId + ' .desc-textarea').val(taskItem.desc);
}

//parse date to server-side style
function parseDate(date) {
	if (date === '') return date;
	return moment(date, 'D.M.YYYY').format('YYYY-MM-DD');
}

//presentation style date
function formatDate(date) {
	if (!date) return '';
	return moment(date, 'YYYY-MM-DD').format('D.M.YYYY');
}

//change order of (-from, -to) date when reverse
function changeDateOrder(parameters, dateType) {
	if (moment(parameters[dateType + '-to']).isBefore(moment(parameters[dateType + '-from']))) {
		var to = parameters[dateType + '-to'];
		parameters[dateType + '-to'] = parameters[dateType + '-from'];
		parameters[dateType + '-from'] = to;
	}
}

//build result html from JSON task list
function createResults (list) {	
	var html = '';
	$.each(list, function(index, item) {
		html = '<tr class=" '
		+ (item.deleted ? 'task-disabled' : 'task-item')
		+ '" id="task-' + item.id + '">'
	    
		+ '<td class="task-resolution-container">'
		+ '<div>DONE</div>'
		+ '<div><input '
		+ (item.deleted ? 'disabled ' : '')
		+ 'type="checkbox" class="task-checkbox-done" '
		+ (item.done?'checked':'')
		+ '></div>'
		+ '</td>'

		+ '<td class="task-desc-container">'
		+ '<span>Created: '
		+ formatDate(item.createDate)
		+ '</span>'
		+ '<span class="task-resolutionDate">Resolved: '
		+ formatDate(item.resolutionDate)
		+ '</span>'
   		+ '<textarea class="desc-textarea form-control custom-control" readonly rows="2" cols="80">'
   		+ item.desc
		+ '</textarea>'
		+ '</td>'

		+ '<td class="task-dueDate-container">'
		+ '<div>Due date:</div>'
		+ formatDate(item.dueDate)
		+ '</td>'

		+ '<td class="task-delete-container">'
		+ '<div>Delete</div>'
		+ '<div>'
		+ '<button '
		+ (item.deleted ? 'disabled ' : '')
		+ 'type="submit" class="btn btn-default delete-task">'
		+ '<img width="52" height="52" alt="find it all!" src="assets/img/rubbish.png">'
		+ '</button>'
		+ '</div>'
		+ '</td>'

		+ '<td class="task-save-container">'
		+ '<div>'
		+ '<button '
		+ (item.deleted ? 'disabled ' : '')
		+ 'type="submit" disabled class="btn btn-default save-task">'
		+ '<img width="30" height="30" alt="find it all!" src="assets/img/check52.png">'
		+ '</button>'
		+ '</div>'
		+ '<div>'
		+ '<button '
		+ (item.deleted ? 'disabled ' : '')
		+ 'type="submit" disabled class="btn btn-default cancel-task">'
		+ '<img width="30" height="30" alt="find it all!" src="assets/img/clear5.png">'
		+ '</button>'
		+ '</div>'
		+ '</td>'
		
		+ '</tr>';	
		$('#task-list').append(html);
	});
//	delay to show loading indicator	
//
//	_.delay(function(msg) {
//		console.log(msg);
//	},5000, 'delay to show loading indicatior');
}

function incrementBadge(taskType) {
	$('#count-' + taskType).text(parseInt($('#count-' + taskType).text()) + 1);
}

function decrementBadge(taskType) {
	$('#count-' + taskType).text(parseInt($('#count-' + taskType).text()) - 1);
}

function deleteObjectFromListByPair(taskList, prop, id) {
	$.each(taskList, function(index, item) {
		 if (item[prop] === parseInt(id)) {
			delete taskList[index]; 
		 } 
	});
	taskList = $.grep(taskList, function(n) {
		  return(n);
	});
	return taskList;
}

function taskItemEnable(element) {
	$('.task-item').removeClass('enabled-task');
	$(element).addClass('enabled-task');
	       
    $('.desc-textarea').each(function() {
    	$(this).attr('readonly', 'readonly');
    	$(this).removeClass('enabled-area-wrapper');
    });
    $('.enabled-task .desc-textarea').each(function() {
    	$(this).removeAttr('readonly');
    	$(this).addClass('enabled-area-wrapper'); 
    });
}

function checkIfNoResults() {
	var noResultsFound = '<div class="outerContainer task-item" id="no-task-found">'
		+ '<span class="innerContainer">No task found</span>'
		+ '</div>';
	if (!$('.task-item').length && !$('.task-disabled').length) {
		$(noResultsFound).insertAfter('#task-list');
	}
}
