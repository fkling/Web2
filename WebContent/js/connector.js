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


function Connector() {};

Connector.prototype = {
	searchForEmails: function(searchParams, callback) {
		log(searchParams);
		callback(result);
	}
};

var gConnector = new Connector();