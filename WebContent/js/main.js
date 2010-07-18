

var log = function(msg) {
	if(console) {
		log = function(msg) {console.log(msg);};
	}
	else {
		log = function(msg){/*do nothing*/};
	}
};

var Application = {
	init: function() {
		$('#table-view').tablesorter()
		this.createSearchField();
		this.addSearchButton();
		
		$(document).delegate('#prev-date', 'click', function() {
			var currentRow = Application.shadowTable.find('tr[rel='+ $('#facebox #msg-id').text() + ']');
			log(currentRow);
			Application.loadMsg(Application.lastResult.mails[currentRow.prev('tr').attr('rel')]);
		});
		$(document).delegate('#next-date', 'click', function() {
			var currentRow = Application.shadowTable.find('tr[rel='+ $('#facebox #msg-id').text() + ']');
			Application.loadMsg(Application.lastResult.mails[currentRow.next('tr').attr('rel')]);
		});
	},
	
	createSearchField: function(node) {
		
		var fieldhtml = '<div class="search-option">';
		fieldhtml += '<span class="add-button button">+</span><span class="remove-button button">-</span>';
		
		fieldhtml += '<span class="search-label">Search in </span>';
		
		fieldhtml += '<select class="field">';
		fieldhtml += '<option value="from">From</option>';
		fieldhtml += '<option value="subject">Subject</option>';
		fieldhtml += '<option value="date">Date</option>';
		fieldhtml += '<option value="content">Content</option>';
		fieldhtml += '</select>';
		
		fieldhtml += '<span class="search-label"> for </span>';
		fieldhtml += '<input type="text" class="search-value" value="" />';
		
		
		fieldhtml += '</div>';
		
		var field = $(fieldhtml);
		
		
		field.children('.add-button').click(function() {
			$(this).siblings('.remove-button').show();
			$(this).hide();
			Application.createSearchField($(this).parent());
		});
		
		field.children('.remove-button').click(function() {
			if($('#search > .search-option').length == 2) {
				$('#search > .search-option').not($(this).parent()).children('.remove-button').hide();
			}
			if($(this).parent().next('.search-option').length == 0) {
				$(this).parent().prev('.search-option').children('.add-button').show();
			}
			$(this).parent().remove();
		});
		
		if($('#search > .search-option').length == 0) {
			field.children('.remove-button').hide();
		}
		
		if(node) {
			node.after(field);
		}
		else {
			field.appendTo('#search');
		}

	},
	
	addSearchButton: function() {
		$('<button>Search!</button>')
		.appendTo('#search')
		.click(function() {
			Application.serachAndDisplayEmails();
		});
	},
	
	serachAndDisplayEmails: function() {
		var searchParams = {};
		
		$('#search > .search-option').each(function() {
			searchParams[$('select', this).val()] = $('.search-value', this).val();
		});
		
		gConnector.searchForEmails(searchParams, function(mails) { Application.displayEmails(mails);});
		
	},
	
	displayEmails: function(result) {
		this.lastResult = result;
		
		$('#table-view > tbody').empty();
		var rowhtml = Array();
		for(var id in result.mails) {
			var mail = result.mails[id];
			rowhtml.push('<tr rel="' + id + '">');
			for(var prop in {'subject':1, 'from':1, 'date':1}) {
				rowhtml.push('<td>' + mail[prop] + '</td>');
			}
			rowhtml.push('</tr>');			
		}
		$(rowhtml.join('')).appendTo('#table-view > tbody');
		
		$('#table-view tbody tr').click(function() {
			var msg_id = $(this).attr('rel');
			var mail = Application.lastResult.mails[msg_id];
			
			$.facebox({div: '#message-view'});
			Application.loadMsg(mail);			
		});
		$('#table-view tbody tr:even').addClass('alt');
		
		$("#table-view").trigger("update"); 
        $("#table-view").show();
        
        this.shadowTable = $('#table-view').clone(false).appendTo('#meta').tablesorter();
        this.shadowTable.trigger('sorton', [[[2,0]]]);
	},
	loadMsg: function(mail) {
		if(mail) {
			$('#facebox #msg-id').text(mail.id);
			$('#facebox #msg-sender').text(mail.from);
			$('#facebox #msg-subject').text(mail.subject);
			$('#facebox #msg-date').text(mail.date);
			$('#facebox #msg-content p').text(mail.content);
		}
	}
};