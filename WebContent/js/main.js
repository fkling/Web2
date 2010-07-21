
var log = function(msg) {
	if (console) {
		log = function(msg) {
			console.log(msg);
		};
	} else {
		log = function(msg) {/* do nothing */
		};
	}
};

var Application = {
	threads: {},
	init : function() {
		$('#table-view table').tablesorter()
		UIBuilder.initUI();
	},

	serachAndDisplayEmails : function() {
		var searchParams = {};

		$('#search .search-option').each(
				function() {
					searchParams[$(this).attr('name')] = $(this).val();
				});

		gConnector
				.searchForEmails(
						searchParams,
						function(result) {
							Application.result = result;

							if (result.count == 0) {
								$('#result-msg').text("No mails found.");
								return;

							} else {
								$('#result-msg').text(
										"Found " + result.count + " mails:");

								var sortedMails = Array();
								for ( var id in result.mails) {
									sortedMails.push(result.mails[id]);
								}

								Application.sortedMails = sortedMails;
								Application.sortedMails.sort(function(a, b) {
									return Date.parse(a.date).compareTo(
											Date.parse(b.date));
								});

								Application.sortedMails
										.forEach(function(mail, index, mails) {
											mail.prevByDate = (index > 0) ? mails[index - 1]
													: null;
											mail.nextByDate = (index < mails.length - 1) ? mails[index + 1]
													: null;
										});

								UIBuilder.displaySearchResult(result.mails);
							}
						});
	},

	getAndDisplayThreadForMail : function(mail) {
		var t = $('#thread-view-' + mail.id);
		if (t.length > 0) {
			var rootId = t.closest('.thread-view-root').children('li').attr('rel');
			UIBuilder.displayThreadFor(mail, Applications.threads[rootId][rootId], Applications.threads[rootId]);
			return;
		}

		gConnector.getThreadForMailId(mail.id, function(result) {
			UIBuilder.displayThreadFor(mail, result.mails[result.root], result.mails);
			Application.threads[result.root] = result.mails;
			var root = result.mails[result.root];
			if(root.children.length > 0)
				root.nextByThread = result.mails[root.children[0]];
			
			for(var i = 0, length = root.children.length;i < length;i++) {
				setPrevNext(result.mails[root.children[i]], root, result.mails);
			}
			
			function setPrevNext(mail, parent, mails) {
				var siblings = parent.children;
				var index = siblings.indexOf(mail.id);
				mail.prevByThread = (index == 0) ? parent : mails[siblings[index-1]];
				
				if(mail.children.length > 0) {
					mail.nextByThread = mails[mail.children[0]];
				}
				else {
					mail.nextByThread = ancestor(mail, mails);
				}
				for(var i = 0, length = mail.children.length;i < length;i++) {
					setPrevNext(mails[mail.children[i]], mail, mails);
				}
			}
			
			function ancestor(mail, mails) {
				if (mail.parentID == null)
					return null;

				var parent = mails[mail.parentID];
				var index = parent.children.indexOf(mail.id);
				if (index < parent.children.length - 1) {
					return mails[parent.children[index + 1]];
				} else {
					return ancestor(parent, mails);
				}
			}
			
			
		});
	},

	getNextByThread : function(mail) {
		UIBuilder.showMsg(mail.nextByThread);
	},
	getPrevByThread : function(mail) {
		UIBuilder.showMsg(mail.prevByThread);
	}
};