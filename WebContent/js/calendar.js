	moment.locale('sk');

	var pikadayConfig = {
	        showWeekNumber: true,
	        firstDay: 1,
	        maxDate: new Date('2100-12-31'),
	        yearRange: [2014, 2100],
	        format: 'D.M.YYYY',
	        i18n: {
		        previousMonth : 'Predchádzajúci mesiac',
		        nextMonth     : 'Nasledovný mesiac',
		        months        : ['Január','Február','Marec','Apríl','Máj','Jún','Júl','August','September','Október','November','December'],
		        weekdays      : ['Nedeľa','Pondelok','Utorok','Streda','Štvrtok','Piatok','Sobota'],
		        weekdaysShort : ['Ned','Pon','Uto','Str','Štv','Pia','Sob']
	        }
	    };
	var datePickers = [];
	
	$('.datepicker').each(function() {
		pikadayConfig['field'] = $(this)[0];
		pikadayConfig['minDate'] = new Date('1900-01-01');	
	    datePickers[$(this).attr('id')] = new Pikaday(pikadayConfig);
	});
	
	pikadayConfig['field'] = $('#datepicker-due_date-new_task');	
	pikadayConfig['minDate'] = moment().toDate();
    datePickers['#datepicker-due_date-new_task'] = new Pikaday(pikadayConfig);
    