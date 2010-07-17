var result = {
	count: 5,
	mails: {
		1: {id: 1, from: 'Christina', subject: 'hello', content: 'nothing here', date: '12-03-2010', parent: null},
		2: {id: 2, from: 'Felix', subject: 're: hello', content: 'nothing here too', date: '13-03-2010', parent: 1},
		3: {id: 3, from: 'Arber', subject: 're: hello', content: 'nothing here...really?', date: '13-03-2010', parent: 1},
		4: {id: 4, from: 'Christina', subject: 're: hello', content: 'really!', date: '14-03-2010', parent: 3},
		5: {id: 5, from: 'Felix', subject: 'oh boy', content: 'this damn project', date: '17-03-2010', parent: null}
	}
};



var Application = {
	init: function() {
		$('#table-view').tablesorter();
		this.createSearchField();
		this.addSearchButton();
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
		
		this.searchEmails(searchParams, function(mails) { Application.displayEmails(mails);});
		
	},
	
	searchEmails: function(searchParams, callback) {
		console.log(searchParams);
		callback(result);
	},
	
	displayEmails: function(result) {
		this.lastResult = result;
		
		$('#table-view > tbody').empty();
		for(var id in result.mails) {
			var mail = result.mails[id];
			var rowhtml = Array();
			rowhtml.push('<tr>');
			for(var prop in {'subject':1, 'from':1, 'date':1}) {
				rowhtml.push('<td>' + mail[prop] + '</td>');
			}
			rowhtml.push('</tr>');
			
			$(rowhtml.join('')).appendTo('#table-view > tbody');
			
			$("#table-view").trigger("update"); 
            $("#table-view").show();
		}
	}
};