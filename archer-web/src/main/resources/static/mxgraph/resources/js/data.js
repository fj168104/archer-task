const elements = [
	{
		// id: 'd1',
		type: 'TASK',
		typeName: '程序任务',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/gear.png',
		isControllNode: false,
		style: '',
		taskClass: 'com.mr.xxx.TaskDemo',
		contentFunc: createTaskContent
	},

	{
		// id: 'd1',
		type: 'USER',
		typeName: '用户任务',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/keys.png',
		isControllNode: false,
		style: 'fillColor=#FF99CC',
		operationTag: '',
		contentFunc: createUserContent
	},

	{
		// id: 'd2',
		type: 'MAIL',
		typeName: '邮件',
		typeDesp: '',
		image: 'mxgraph/resources/images/icons48/mail_new.png',
		isControllNode: false,
		style: '',
		to: '',
		cc: '',
		from: '',
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
		isControllNode: false,
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

const taskFunc = {
	'TASK': createTaskContent,
	'USER': createUserContent,
	'MAIL': createMailContent,
	'START': createStartContent,
	'ERROR': createErrorContent,
	'MERGE': createMergeContent
}


function createStartContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);

	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
	};
	return content;
}

function createErrorContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);
	addInputElement(table, 'input', '异常处理类', this.getValue().errorClass);
	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.value.errorClass = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createMergeContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);
	addInputElement(table, 'input', '路由条件', this.getValue().condition);
	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.value.condition = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createTaskContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);
	addInputElement(table, 'input', '任务处理类', this.getValue().taskClass);
	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.value.taskClass = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createUserContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);
	addInputElement(table, 'input', '用户操作Tag', this.getValue().operationTag);
	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.value.operationTag = content.getElementsByTagName('input')[1].value;
	};
	return content;
}

function createMailContent() {
	let content = document.createElement('div');
	let table = document.createElement('table');

	addInputElement(table, 'input', '名称', this.getValue().typeName, true);
	addInputElement(table, 'textarea', '描述', this.getValue().typeDesp, false, 3);
	addInputElement(table, 'input', '收件人', this.getValue().To);
	addInputElement(table, 'input', '抄送人', this.getValue().Cc);
	addInputElement(table, 'input', '发件人', this.getValue().From);
	content.appendChild(table);

	content.func = function (cell) {
		cell.value.typeDesp = content.getElementsByTagName('textarea')[0].value;
		cell.value.To = content.getElementsByTagName('input')[1].value;
		cell.value.Cc = content.getElementsByTagName('input')[2].value;
		cell.value.From = content.getElementsByTagName('input')[3].value;
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
