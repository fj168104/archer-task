const elements = [
	{
		// id: 'd1',
		type: 'TASK',
		typeName: '任务',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/gear.png',
		isControllNode: false,
		style: '',
		taskClass: 'com.mr.xxx.TaskDemo',
		contentFunc: createTaskContent
	},

	{
		// id: 'd2',
		type: 'MAIL',
		typeName: '邮件',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/mail_new.png',
		isControllNode: false,
		style: '',
		To: '',
		Cc: '',
		From: '',
		contentFunc: createMailContent
	},

	{
		// id: 'd3',
		type: 'START',
		typeName: '开始',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/event.png',
		isControllNode: true,
		style: 'fillColor=#cdeb8b;shape=ellipse',
		contentFunc: createStartContent
	},

	{
		// id: 'd4',
		type: 'END',
		typeName: '结束',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/event_end.png',
		isControllNode: true,
		style: 'fillColor=#ffc7c7;shape=ellipse'
	},

	{
		// id: 'd5',
		type: 'ERROR',
		typeName: '异常',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/error.png',
		isControllNode: true,
		style: 'fillColor=#f52212;shape=ellipse',
		errorClass: 'com.mr.xxx.ErrorDemo',
		contentFunc: createErrorContent
	},

	{
		// id: 'd6',
		type: 'MERGE',
		typeName: '分支',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/merge.png',
		isControllNode: true,
		style: 'fillColor=#91bcc0;shape=ellipse',
		condition: '',
		contentFunc: createMergeContent
	},
];


function createStartContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.data.typeName, true);
	addInputElement(table, 'textarea', '描述', this.data.typeDesp, false, 3);

	content.appendChild(table);

	content.func = function (cell) {
		cell.data.typeDesp = content.getElementsByTagName('textarea')[0].value;
	};
	return content;
}

const taskFunc = {
	'TASK': createTaskContent,
	'MAIL': createMailContent,
	'START': createStartContent,
	'ERROR': createErrorContent,
	'MERGE': createMergeContent
}

function createErrorContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.data.typeName, true);
	addInputElement(table, 'textarea', '描述', this.data.typeDesp, false, 3);
	addInputElement(table, 'input', '异常处理类', this.data.errorClass);
	content.appendChild(table);

	content.func = function (cell) {
		cell.data.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.data.errorClass = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createMergeContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.data.typeName, true);
	addInputElement(table, 'textarea', '描述', this.data.typeDesp, false, 3);
	addInputElement(table, 'input', '路由条件', this.data.condition);
	content.appendChild(table);

	content.func = function (cell) {
		cell.data.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.data.condition = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createTaskContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.data.typeName, true);
	addInputElement(table, 'textarea', '描述', this.data.typeDesp, false, 3);
	addInputElement(table, 'input', '任务处理类', this.data.taskClass);
	content.appendChild(table);

	content.func = function (cell) {
		cell.data.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.data.taskClass = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createMailContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.data.typeName, true);
	addInputElement(table, 'textarea', '描述', this.data.typeDesp, false, 3);
	addInputElement(table, 'input', '收件人', this.data.To);
	addInputElement(table, 'input', '抄送人', this.data.Cc);
	addInputElement(table, 'input', '发件人', this.data.From);
	content.appendChild(table);

	content.func = function (cell) {
		cell.data.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.data.To = content.getElementsByTagName('input')[1].value;
		cell.data.Cc = content.getElementsByTagName('input')[2].value;
		cell.data.From = content.getElementsByTagName('input')[3].value;
	};
	return content;
}


function addInputElement(table, type, name, value, readonly, rows) {
	let tr = document.createElement('tr');

	let tdLabel = document.createElement('td');
	tdLabel.innerHTML = name;
	tr.appendChild(tdLabel);

	let td = document.createElement('td');
	let inputData = document.createElement(type);
	if (type === 'textarea') {
		inputData.innerText = value;
	} else {
		inputData.setAttribute('value', value);
	}

	if (readonly) {
		inputData.setAttribute('readonly', readonly);
	}

	if (rows) {
		inputData.setAttribute('rows', rows);
	}

	td.appendChild(inputData);
	tr.appendChild(td);

	table.appendChild(tr);
}
