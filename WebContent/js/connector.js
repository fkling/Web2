var result = {
	count : 5,
	mails : {
		1 : {
			id : 1,
			from : 'Christina',
			subject : 'hello',
			content : 'nothing here',
			date : '2010-03-12',
			parent : null
		},
		2 : {
			id : 2,
			from : 'Felix',
			subject : 're: hello',
			content : 'nothing here too',
			date : '2010-03-13',
			parent : 1
		},
		3 : {
			id : 3,
			from : 'Arber',
			subject : 're: hello',
			content : 'nothing here...really?',
			date : '2010-03-13',
			parent : 1
		},
		4 : {
			id : 4,
			from : 'Christina',
			subject : 're: hello',
			content : 'really!',
			date : '2010-03-14',
			parent : 3
		},
		5 : {
			id : 5,
			from : 'Felix',
			subject : 'oh boy',
			content : 'this damn project',
			date : '2010-03-17',
			parent : null
		}
	}
};

var result_thread = {
	count : 4,
	mails : {
		1 : {
			id : 1,
			from : 'Christina',
			subject : 'hello',
			content : 'nothing here',
			date : '2010-03-12',
			children :[2, 3],
			parentID: null
		},
		2 : {
			id : 2,
			from : 'Felix',
			subject : 're: hello',
			content : 'nothing here too',
			date : '2010-03-13',
			children : [],
			parentID: 1
		},
		3 : {
			id : 3,
			from : 'Arber',
			subject : 're: hello',
			content : 'nothing here...really?',
			date : '2010-03-13',
			children: [4],
			parentID: 1
		},
		4 : {
			id : 4,
			from : 'Christina',
			subject : 're: hello',
			content : 'really!',
			date : '2010-03-14',
			children: [],
			parentID: 3
		}
	},
	root : 1
};

function Connector() {
};

Connector.prototype = {
	searchForEmails : function(searchParams, callback) {
		$.get('rest/mails/search', searchParams, callback, 'json');
	},
	
	getThreadForMailId: function(mail_id, callback) {
		$.get('rest/mails/getThread', {id: mail_id}, callback, 'json');
	}
};

var gConnector = new Connector();