var UIBuilder = {
	initUI: function() {
		this.createSearchField();
		this.initDatePicker();
		this.setUpSearchButton();
		this.initMsgView();
		this.initThreadView();
		this.initTableView();
	},
	createSearchField: function() {		
		$('#search').show();
	},
	
	setUpSearchButton: function() {
		$('#search-button')
		.click(function() {
			Application.serachAndDisplayEmails();
		});
	},
	
	initDatePicker: function() {
		var options = {
			presetRanges: [
			       {text: 'Today', dateStart: 'today', dateEnd: 'today' },
			       {text: 'Last 7 days', dateStart: 'today-7days', dateEnd: 'today' },
			       {text: 'This month', dateStart: function(){ return Date.parse('today').moveToFirstDayOfMonth();  }, dateEnd: 'today' },
			       {text: 'Last month', dateStart: function(){ return Date.parse('last month').moveToFirstDayOfMonth();  }, dateEnd: function(){ return Date.parse('last month'); } },
			       {text: 'Last 2 months', dateStart: function(){ return Date.parse('last month').moveToFirstDayOfMonth();  }, dateEnd: 'today' },
			       {text: 'Last 3 months', dateStart: function(){ return Date.parse('today - 2 months').moveToFirstDayOfMonth();  }, dateEnd: 'today' }
			],
			dateFormat: 'yy-mm-dd'
		};
		$('input.date').daterangepicker(options); 
		
	},
	
	initThreadView: function() {
		$('#back-to-results').click(function() {
			$('.thread-view').remove();
			$('#back-to-results').hide();
			$('#facebox_overlay').click();
			$('#table-view').show();
		});
		$(document).delegate('.thread-view li > span', 'click', function(e) {
			var threadMails = Application.threads[$(this).closest('.thread-view-root').children('li').attr('rel')];
			
			$('#next-thread, #prev-thread, #thread-icon').show();
			$('#next-date, #date-icon, #prev-date, #thread-all-icon').hide();
			$.facebox({div: '#message-view'});
			UIBuilder.showMsg(threadMails[$(this).parent().attr('rel')]);
			e.preventDefault();
		});
	},
	
	initTableView: function() {
		$('#table-view').delegate('tbody tr','click', function() {
			var msg_id = $(this).attr('rel');
			var mail = Application.result.mails[msg_id];
			
			$('#next-thread, #prev-thread, #thread-icon').hide();
			$('#next-date, #date-icon, #prev-date, #thread-all-icon').show();
			$.facebox({div: '#message-view'});
			UIBuilder.showMsg(mail);			
		});
	},
	
	displaySearchResult: function(mails) {
		
		$('#results').show();
		$('#table-view > table > tbody').empty();
		var rowhtml = Array();
		for(var id in mails) {
			var mail = mails[id];
			rowhtml.push('<tr rel="' + id + '">');
			for(var prop in {'subject':1, 'from':1, 'date':1}) {
				rowhtml.push('<td>' + mail[prop] + '</td>');
			}
			rowhtml.push('</tr>');			
		}
		$(rowhtml.join('')).appendTo('#table-view > table > tbody');
		
		$('#table-view tbody tr:even').addClass('alt');
		
		$("#table-view table").trigger("update"); 
        $("#table-view").show();
	},
	
	initMsgView: function() {
		$(document).delegate('#prev-date', 'click', function(e) {
			UIBuilder.showMsg($('#facebox').data('mail').prevByDate);
			e.preventDefault();
		});
		$(document).delegate('#next-date', 'click', function(e) {
			UIBuilder.showMsg($('#facebox').data('mail').nextByDate);
			e.preventDefault();
		});
		
		$(document).delegate('#prev-thread', 'click', function(e) {
			Application.getPrevByThread($('#facebox').data('mail'));
			e.preventDefault();
		});
		$(document).delegate('#next-thread', 'click', function(e) {
			Application.getNextByThread($('#facebox').data('mail'));
			e.preventDefault();
		});
		
		$(document).delegate('#thread-all-icon', 'click', function(e) {
			Application.getAndDisplayThreadForMail($('#facebox').data('mail'));
		});
	},
	
	showMsg: function(mail) {
		if(mail) {
			$('#facebox').data('mail', mail);
			$('#facebox #msg-id').text(mail.id);
			$('#facebox #msg-sender').text(mail.from);
			$('#facebox #msg-subject').text(mail.subject);
			$('#facebox #msg-date').text(mail.date);
			$('#facebox #msg-content p').text(mail.content);
		}
	},	
	
	displayThreadFor: function(mail, root, mails) {
		$('#table-view').hide();
		$('#back-to-results').show();
		
		$('<ul class="thread-view-root thread-view" />')
		.append(this.createThreadViewFor(root, mail, mails))
		.appendTo('#results');
		$('#facebox_overlay').click();
	},
	
	createThreadViewFor: function(mail, currentMail, mails) {
		var $li = $('<li />')
		.attr('id', 'thread-view-' + mail.id)
		.attr('rel', mail.id)
		.append('<span>' + [mail.subject, mail.from, mail.date].join(' | ') + '</span>');
		
		if(mail.id == currentMail.id) {
			$li.addClass('current');
		}
		
		if(mail.children.length > 0) {
			var children = mail.children
			var $ul = $('<ul />');	
			for(var i = 0, length = children.length; i < length; ++i) {
				$ul.append(this.createThreadViewFor(mails[children[i]], currentMail, mails));
			}
			$ul.appendTo($li);
		}
		return $li;
	}
}