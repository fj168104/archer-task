<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Row @keydown.enter.native="handleSearch">
            <Form ref="searchForm" :model="searchForm" inline :label-width="70" class="search-form">
                <Form-item label="名称" prop="name">
                  <Input type="text" v-model="searchForm.name" placeholder="请输入名称" clearable style="width: 200px"/>
                </Form-item>
                <Form-item label="数据库类型" prop="type">
                  <Select v-model="searchForm.type" style="width:250px" clearable placeholder="请选择">
                    <Option value="">请选择</Option>
                    <Option value="mysql">mysql</Option>
                    <Option value="oracle">oracle</Option>
                  </Select>
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
            <Table :loading="loading" border :columns="columns" :data="data" ref="table" sortable="custom" @on-sort-change="changeSort" @on-selection-change="changeSelect"></Table>
          </Row>
          <Row type="flex" justify="end" class="page">
            <Page :current="searchForm.pageNumber" :total="total" :page-size="searchForm.pageSize" @on-change="changePage" @on-page-size-change="changePageSize" :page-size-opts="[10,20,50]" size="small" show-total show-elevator show-sizer></Page>
          </Row>
        </Card>
      </Col>
    </Row>
    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable='false' :width="770">
      <Form ref="form" :model="form" :label-width="100" :rules="formValidate" inline>
        <FormItem label="数据源编号" prop="id" >
          <Input v-model="form.id" style="width:250px"/>
        </FormItem>
        <FormItem label="名称" prop="name" >
          <Input v-model="form.name" style="width:250px"/>
        </FormItem>
        <FormItem label="数据库类型" prop="type" >
          <Select v-model="form.type" style="width:250px">
            <Option value="mysql">mysql</Option>
            <Option value="oracle">oracle</Option>
          </Select>
        </FormItem>
        <FormItem label="驱动名称" prop="driverClassName" >
          <Input v-model="form.driverClassName" style="width:250px"/>
        </FormItem>
        <FormItem label="url" prop="url" >
          <Input v-model="form.url" style="width:250px"/>
        </FormItem>
        <FormItem label="用户名" prop="username" >
          <Input v-model="form.username" style="width:250px"/>
        </FormItem>
        <FormItem label="密码" prop="password" >
          <Input v-model="form.password" style="width:250px"/>
        </FormItem>
        <FormItem label="初始连接数" prop="initialSize" >
          <InputNumber v-model="form.initialSize" style="width:250px"></InputNumber>
        </FormItem>
        <FormItem label="最大连接数" prop="maxActive" >
          <InputNumber v-model="form.maxActive" style="width:250px"></InputNumber>
        </FormItem>
        <FormItem label="最大建立连接等待时间" prop="maxWait" >
          <InputNumber v-model="form.maxWait" style="width:250px" :formatter="value => `${value}毫秒`">
          </InputNumber>
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
import {
  getDatabaseConfigList
  ,addDatabaseConfig
  ,editDatabaseConfig
  ,deleteDatabaseConfig
  ,mydeleteDatabaseConfigs
  ,initDatabaseConfig
  ,closeDatabaseConfig
  ,restartDatabaseConfig
} from "@/api/data";

export default {
  name: "database-config",
  components: {
  },
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
        type: "",
        name: ""
      },
      form: { // 添加或编辑表单对象初始化数据
        id: "",
        name: "",
        type: "",
        driverClassName: "",
        url: "",
        username: "",
        password: "",
        initialSize: 0,
        maxActive: 0,
        maxWait: 0,
      },
      // 表单验证规则
      formValidate: {
        id: [{ required: true, message: "不能为空", trigger: "blur" }],
        name: [{ required: true, message: "不能为空", trigger: "blur" }],
        type: [{ required: true, message: "不能为空", trigger: "blur" }],
        driverClassName: [{ required: true, message: "不能为空", trigger: "blur" }],
        url: [{ required: true, message: "不能为空", trigger: "blur" }],
        username: [{ required: true, message: "不能为空", trigger: "blur" }],
        password: [{ required: true, message: "不能为空", trigger: "blur" }],
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
          title: "数据源编号",
          key: "id",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "名称",
          key: "name",
          minWidth: 220,
          sortable: false,
        },
        {
          title: "数据库类型",
          key: "type",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "状态",
          key: "status",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "驱动名称",
          key: "driverClassName",
          minWidth: 220,
          sortable: false,
        },
        /*{
          title: "url",
          key: "url",
          minWidth: 320,
          sortable: false,
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
        },*/
        {
          title: "初始连接数",
          key: "initialSize",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "最大连接数",
          key: "maxActive",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "连接等待时间（毫秒）",
          key: "maxWait",
          minWidth: 120,
          sortable: false,
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 250,
          render: (h, params) => {
            let btns = [
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
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.remove(params.row);
                    }
                  }
                },
                "删除"
              )
            ];
            if ( '未初始化' == params.row.status) {
              btns.push(
                h(
                  "Button",
                  {
                    props: {
                      type: "primary",
                      size: "small",
                      icon: "md-checkmark"
                    },
                    on: {
                      click: () => {
                        this.initpool(params.row);
                      }
                    }
                  },
                  "初始化"
                )
              );
            } else if ( '已关闭' == params.row.status) {
              btns.push(
                h(
                  "Button",
                  {
                    props: {
                      type: "primary",
                      size: "small",
                      icon: "md-checkmark"
                    },
                    on: {
                      click: () => {
                        this.restartpool(params.row);
                      }
                    }
                  },
                  "启用"
                )
              );
            } else if ( '已启用' == params.row.status) {
              btns.push(
                h(
                  "Button",
                  {
                    props: {
                      type: "primary",
                      size: "small",
                      icon: "md-close"
                    },
                    on: {
                      click: () => {
                        this.closepool(params.row);
                      }
                    }
                  },
                  "关闭"
                )
              );
            }
            return h("div", btns);
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
      // this.resetSearchData();
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
      console.log(this.searchForm);
      console.log(!this.searchForm.type);
      if (!this.searchForm.type) {
        this.searchForm.type = "";
      }
      console.log(this.searchForm);
      getDatabaseConfigList(this.searchForm).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result.content;
          this.total = res.result.totalElements;
        }
      });
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          if (this.modalType === 0) {
            // 添加 避免编辑后传入id等数据 记得删除
            addDatabaseConfig(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getDataList();
                this.modalVisible = false;
              }
            });
          } else {
            // 编辑
            editDatabaseConfig(this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.getDataList();
                this.modalVisible = false;
              }
            });
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
          deleteDatabaseConfig(v.id).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    initpool(v) {
      this.$Modal.confirm({
        title: "确认初始化",
        content: "您确认要初始化 " + v.name + " ?",
        loading: true,
        onOk: () => {
          initDatabaseConfig({id:v.id}).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    restartpool(v) {
      this.$Modal.confirm({
        title: "确认启用",
        content: "您确认要启用 " + v.name + " ?",
        loading: true,
        onOk: () => {
          restartDatabaseConfig({id:v.id}).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    closepool(v) {
      this.$Modal.confirm({
        title: "确认关闭",
        content: "您确认要关闭 " + v.name + " ?",
        loading: true,
        onOk: () => {
          closeDatabaseConfig({id:v.id}).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
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
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          // 批量删除
          mydeleteDatabaseConfigs(ids).then(res => {
            this.$Modal.remove();
            if (res.success) {
              this.$Message.success("操作成功");
              this.clearSelectAll();
              this.getDataList();
            }
          });
        }
      });
    },
    resetSearchData() {
      this.searchForm =  { // 搜索框初始化对象
        pageNumber: 1, // 当前页数
        pageSize: 10, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc", // 默认排序方式
        type: "",
        name: ""
      }
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
