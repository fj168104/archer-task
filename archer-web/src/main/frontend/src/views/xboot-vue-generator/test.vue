<template>
    <div class="search">
        <Row>
            <Col>
                <Card>
                    <Row @keydown.enter.native="handleSearch">
                        <Form ref="searchForm" :model="searchForm" inline :label-width="70" class="search-form">
                            <Form-item label="用户名" prop="username">
                                <Input type="text" v-model="searchForm.username" placeholder="请输入用户名" clearable
                                       style="width: 200px"/>
                            </Form-item>
                            <Form-item label="密码" prop="password">
                                <Input type="text" v-model="searchForm.password" placeholder="请输入密码" clearable
                                       style="width: 200px"/>
                            </Form-item>
                            <Form-item style="margin-left:-35px;" class="br">
                                <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                                <Button @click="handleReset">重置</Button>
                            </Form-item>
                        </Form>
                    </Row>
                    <Row class="operation">
                        <Button @click="add" type="primary" icon="md-add">添加</Button>
                        <Button @click="delAll" icon="md-trash">批量删除</Button>
                        <Button @click="getDataList" icon="md-refresh">刷新</Button>
                    </Row>
                    <Row>
                        <Alert show-icon>
                            已选择 <span class="select-count">{{selectCount}}</span> 项
                            <a class="select-clear" @click="clearSelectAll">清空</a>
                        </Alert>
                    </Row>
                    <Row>
                        <Table :loading="loading" border :columns="columns" :data="data" ref="table" sortable="custom"
                               @on-sort-change="changeSort" @on-selection-change="changeSelect"></Table>
                    </Row>
                    <Row type="flex" justify="end" class="page">
                        <Page :current="searchForm.pageNumber" :total="total" :page-size="searchForm.pageSize"
                              @on-change="changePage" @on-page-size-change="changePageSize" :page-size-opts="[10,20,50]"
                              size="small" show-total show-elevator show-sizer></Page>
                    </Row>
                </Card>
            </Col>
        </Row>
        <Modal :title="modalTitle" v-model="modalVisible" :mask-closable='false' :width="500">
            <Form ref="form" :model="form" :label-width="100" :rules="formValidate">
                <FormItem label="用户名" prop="username">
                    <Input v-model="form.username" style="width:100%"/>
                </FormItem>
                <FormItem label="密码" prop="password">
                    <Input v-model="form.password" style="width:100%"/>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="text" @click="modalVisible=false">取消</Button>
                <Button type="primary" :loading="submitLoading" @click="handleSubmit">提交</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
	export default {
		name: "archer",
		components: {},
		data() {
			return {
				loading: true, // 表单加载状态
				modalType: 0, // 添加或编辑标识
				modalVisible: false, // 添加或编辑显示
				modalTitle: "", // 添加或编辑标题
				searchForm: { // 搜索框初始化对象
					pageNumber: 1, // 当前页数
					pageSize: 10, // 页面大小
					sort: "createTime", // 默认排序字段
					order: "desc", // 默认排序方式
				},
				form: { // 添加或编辑表单对象初始化数据
					username: "",
					password: "",
				},
				// 表单验证规则
				formValidate: {
					username: [{required: true, message: "不能为空", trigger: "blur"}],
				},
				submitLoading: false, // 添加或编辑提交状态
				selectList: [], // 多选数据
				selectCount: 0, // 多选计数
				columns: [
					// 表头
					{
						type: "selection",
						width: 60,
						align: "center"
					},
					{
						type: "index",
						width: 60,
						align: "center"
					},
					{
						title: "用户名",
						key: "username",
						minWidth: 120,
						sortable: false,
					},
					{
						title: "密码",
						key: "password",
						minWidth: 120,
						sortable: false,
					},
					{
						title: "操作",
						key: "action",
						align: "center",
						width: 200,
						render: (h, params) => {
							return h("div", [
								h(
									"Button",
									{
										props: {
											type: "primary",
											size: "small",
											icon: "ios-create-outline"
										},
										style: {
											marginRight: "5px"
										},
										on: {
											click: () => {
												this.edit(params.row);
											}
										}
									},
									"编辑"
								),
								h(
									"Button",
									{
										props: {
											type: "error",
											size: "small",
											icon: "md-trash"
										},
										on: {
											click: () => {
												this.remove(params.row);
											}
										}
									},
									"删除"
								)
							]);
						}
					}
				],
				data: [], // 表单数据
				total: 0 // 表单数据总数
			};
		},
		methods: {
			init() {
				this.getDataList();
			},
			changePage(v) {
				this.searchForm.pageNumber = v;
				this.getDataList();
				this.clearSelectAll();
			},
			changePageSize(v) {
				this.searchForm.pageSize = v;
				this.getDataList();
			},
			handleSearch() {
				this.searchForm.pageNumber = 1;
				this.searchForm.pageSize = 10;
				this.getDataList();
			},
			handleReset() {
				this.$refs.searchForm.resetFields();
				this.searchForm.pageNumber = 1;
				this.searchForm.pageSize = 10;
				// 重新加载数据
				this.getDataList();
			},
			changeSort(e) {
				this.searchForm.sort = e.key;
				this.searchForm.order = e.order;
				if (e.order === "normal") {
					this.searchForm.order = "";
				}
				this.getDataList();
			},
			clearSelectAll() {
				this.$refs.table.selectAll(false);
			},
			changeSelect(e) {
				this.selectList = e;
				this.selectCount = e.length;
			},
			getDataList() {
				this.loading = true;
				// 带多条件搜索参数获取表单数据 请自行修改接口
				this.getRequest("/archer/getByCondition", this.searchForm).then(res => {
				  this.loading = false;
				  if (res.success) {
				    this.data = res.result.content;
				    this.total = res.result.totalElements;
				  }
				});
				以下为模拟数据
				this.data = [
				];
				this.total = this.data.length;
				this.loading = false;
			},
			handleSubmit() {
				this.$refs.form.validate(valid => {
					if (valid) {
						this.submitLoading = true;
						if (this.modalType === 0) {
							// 添加 避免编辑后传入id等数据 记得删除
							delete this.form.id;
							// this.postRequest("请求地址", this.form).then(res => {
							//   this.submitLoading = false;
							//   if (res.success) {
							//     this.$Message.success("操作成功");
							//     this.getDataList();
							//     this.modalVisible = false;
							//   }
							// });
							// 模拟请求成功
							this.submitLoading = false;
							this.$Message.success("操作成功");
							this.getDataList();
							this.modalVisible = false;
						} else {
							// 编辑
							// this.postRequest("请求地址", this.form).then(res => {
							//   this.submitLoading = false;
							//   if (res.success) {
							//     this.$Message.success("操作成功");
							//     this.getDataList();
							//     this.modalVisible = false;
							//   }
							// });
							// 模拟请求成功
							this.submitLoading = false;
							this.$Message.success("操作成功");
							this.getDataList();
							this.modalVisible = false;
						}
					}
				});
			},
			add() {
				this.modalType = 0;
				this.modalTitle = "添加";
				this.$refs.form.resetFields();
				delete this.form.id;
				this.modalVisible = true;
			},
			edit(v) {
				this.modalType = 1;
				this.modalTitle = "编辑";
				this.$refs.form.resetFields();
				// 转换null为""
				for (let attr in v) {
					if (v[attr] === null) {
						v[attr] = "";
					}
				}
				let str = JSON.stringify(v);
				let data = JSON.parse(str);
				this.form = data;
				this.modalVisible = true;
			},
			remove(v) {
				this.$Modal.confirm({
					title: "确认删除",
					// 记得确认修改此处
					content: "您确认要删除 " + v.name + " ?",
					loading: true,
					onOk: () => {
						// 删除
						// this.deleteRequest("请求地址，如/deleteByIds/" + v.id).then(res => {
						//   this.$Modal.remove();
						//   if (res.success) {
						//     this.$Message.success("操作成功");
						//     this.getDataList();
						//   }
						// });
						// 模拟请求成功
						this.$Message.success("操作成功");
						this.$Modal.remove();
						this.getDataList();
					}
				});
			},
			delAll() {
				if (this.selectCount <= 0) {
					this.$Message.warning("您还未选择要删除的数据");
					return;
				}
				this.$Modal.confirm({
					title: "确认删除",
					content: "您确认要删除所选的 " + this.selectCount + " 条数据?",
					loading: true,
					onOk: () => {
						let ids = "";
						this.selectList.forEach(function (e) {
							ids += e.id + ",";
						});
						ids = ids.substring(0, ids.length - 1);
						// 批量删除
						// this.deleteRequest("请求地址，如/deleteByIds/" + ids).then(res => {
						//   this.$Modal.remove();
						//   if (res.success) {
						//     this.$Message.success("操作成功");
						//     this.clearSelectAll();
						//     this.getDataList();
						//   }
						// });
						// 模拟请求成功
						this.$Message.success("操作成功");
						this.$Modal.remove();
						this.clearSelectAll();
						this.getDataList();
					}
				});
			}
		},
		mounted() {
			this.init();
		}
	};
</script>
<style lang="less">
    .search {
        .operation {
            margin-bottom: 2vh;
        }

        .select-count {
            font-size: 13px;
            font-weight: 600;
            color: #40a9ff;
        }

        .select-clear {
            margin-left: 10px;
        }

        .page {
            margin-top: 2vh;
        }

        .drop-down {
            font-size: 13px;
            margin-left: 5px;
        }
    }
</style>
