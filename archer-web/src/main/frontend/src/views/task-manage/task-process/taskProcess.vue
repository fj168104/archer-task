<template>
  <div class="search">
    <Row>
      <Col>
        <Card>

          <Row class="operation">
            <Button @click="getDataList" icon="md-refresh">刷新</Button>
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
    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable='false' :width="500">
      <Form ref="form" :model="form" :label-width="100" >
        <FormItem label="任务执行节点" prop="executeNode" >
          <Input v-model="form.executeNode" style="width:100%" readonly/>
        </FormItem>

        <FormItem label="收集的信号量" prop="nodeSemphones" >
          <Input v-model="form.nodeSemphones" style="width:100%" readonly/>
        </FormItem>

        <FormItem label="上一任务执行节点" prop="preExecuteNodes" >
          <Input v-model="form.preExecuteNodes" style="width:100%" readonly/>
        </FormItem>

        <FormItem label="下一任务执行节点" prop="nextExecuteNodes" >
          <Input v-model="form.nextExecuteNodes" style="width:100%" readonly/>
        </FormItem>
        <FormItem label="运行结果/下一任务所需参数" prop="runResult" >
          <Input v-model="form.runResult" style="width:100%"/ readonly>
        </FormItem>

        <FormItem label="流程状态" prop="status" >
          <Input v-model="form.status" style="width:100%" readonly/>
        </FormItem>

        <FormItem label="执行异常" prop="exception" >
          <Input v-model="form.exception" style="width:100%" readonly/>
        </FormItem>
        <FormItem label="异常描述" prop="errorMessage" >
          <Input v-model="form.errorMessage" style="width:100%" readonly/>
        </FormItem>
      </Form>
      <
    </Modal>
  </div>
</template>

<script>
  import {
    getProcessList
  } from "@/api/task";

  export default {
    name: "task-process",
    components: {
    },
    props:{
      taskId: {
        type: String,
        default: ''
      },

      columeData: {
        type: Array,
        default: []
      }
    },

    data() {
      return {
        loading: true, // 表单加载状态
        modalType: 0, // 添加或编辑标识
        modalVisible: false, // 添加或编辑显示
        modalTitle: "", // 添加或编辑标题
        drop: false,
        dropDownContent: "展开",
        dropDownIcon: "ios-arrow-down",
        searchForm: { // 搜索框初始化对象
          pageNumber: 1, // 当前页数
          pageSize: 10, // 页面大小
          taskId: "",
          sort: "createTime", // 默认排序字段
          order: "asc", // 默认排序方式
        },
        form: { // 添加或编辑表单对象初始化数据
          taskId: "",
          executeNode: "",
          nodeSemphones: "",
          preExecuteNodes: "",
          nextExecuteNodes: "",
          runResult: "",
          status: "",
          finished: "",
          exception: "",
          errorMessage: "",
        },

        columns: [
          {
            type: "index",
            width: 60,
            align: "center"
          },

          {
            title: "任务执行节点",
            key: "executeNode",
            minWidth: 120,
            sortable: true,
          },

          {
            title: "信号量",
            key: "nodeSemphones",
            minWidth: 120,
            sortable: true,
          },

          {
            title: "上一任务执行节点",
            key: "preExecuteNodes",
            minWidth: 120,
            sortable: false,
          },

          {
            title: "下一任务执行节点",
            key: "nextExecuteNode",
            minWidth: 120,
            sortable: false,
          },
          {
            title: "运行结果",
            key: "runResult",
            minWidth: 120,
            sortable: false,
          },
          {
            title: "流程状态",
            key: "status",
            minWidth: 120,
            sortable: false,
          },
          {
            title: "执行异常",
            key: "exception",
            minWidth: 120,
            sortable: false,
          },
          {
            title: "异常描述",
            key: "errorMessage",
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
                              this.showDetail(params.row);
                            }
                          }
                        },
                        "详情"
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
      dropDown() {
        if (this.drop) {
          this.dropDownContent = "展开";
          this.dropDownIcon = "ios-arrow-down";
        } else {
          this.dropDownContent = "收起";
          this.dropDownIcon = "ios-arrow-up";
        }
        this.drop = !this.drop;
      },
      getDataList() {
        this.searchForm.taskId = this.taskId;
        this.loading = true;
        // 带多条件搜索参数获取表单数据 请自行修改接口
        getProcessList(this.searchForm).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result.content;
            this.total = res.result.totalElements;
          }
        });
      },

      showDetail(v) {
        this.modalType = 1;
        this.modalTitle = "详情";
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
