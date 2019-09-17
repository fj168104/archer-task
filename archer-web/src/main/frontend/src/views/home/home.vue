<style lang="less">
@import "./home.less";
@import "../../styles/common.less";
</style>

<template>
    <div>
        <div v-show="currNav=='xboot'">
            <Row :gutter="10">
                <Col :md="24" :lg="8">
                <Row class-name="home-page-row1" :gutter="10">
                    <Col :md="12" :lg="24" :style="{marginBottom: '10px'}">
                    <Card>
                        <Row type="flex" class="user-infor">
                            <Col span="8">
                            <Row class-name="made-child-con-middle" type="flex" align="middle">
                                <img class="avator-img" :src="avatarPath" />
                            </Row>
                            </Col>
                            <Col span="16" style="padding-left:6px;">
                            <Row class-name="made-child-con-middle" type="flex" align="middle">
                                <div>
                                    <b class="card-user-infor-name">{{ username }}</b>
                                    <p>Archer 欢迎您的使用</p>
                                </div>
                            </Row>
                            </Col>
                        </Row>
                        <div class="line-gray"></div>
                        <Row class="margin-top-8">
                            <Col span="8">
                            <p class="notwrap">本次登录地点:</p>
                            </Col>
                            <Col span="16" class="padding-left-8">{{city}}</Col>
                        </Row>
                        <Row class="margin-top-8">
                            <Col span="8">
                            <p class="notwrap">天气:</p>
                            </Col>
                            <Col span="16" class="padding-left-8">{{weather}}</Col>
                        </Row>
                    </Card>
                    </Col>
                    <Col :md="12" :lg="24" style="margin-bottom: 10px;">
                    <Card>
                        <p slot="title" class="card-title">
                            <Icon type="logo-github" size="20" style="margin-bottom:3px;" />
                            <a href="https://github.com/Exrick/x-boot" target="_blank">
                                                Microrule 产品系列
                                            </a>
                        </p>
                        <p slot="extra">
                            <a target="_blank" href="https://www.microrule.com">版权：迈融</a>
                        </p>
                        <div class="to-do-list-con">
                            <Timeline>
                                <TimelineItem>
                                    <Icon type="logo-youtube" color="#fb7299" slot="dot"></Icon>
                                    <a href="https://www.microrule.com/product/risklist.html" target="_blank">规则引擎
                                                    </a>
                                </TimelineItem>
                                <TimelineItem>
                                    <Icon type="md-barcode" color="black" slot="dot"></Icon>
                                    <a href="https://www.microrule.com/product/risklist.html" target="_blank">数据平台
                                                    </a>
                                </TimelineItem>
                                <TimelineItem>
                                    <Icon type="md-cart" color="#f4364c" slot="dot"></Icon>
                                    <a href="https://www.microrule.com/product/risklist.html" target="_blank">风控小程序
                                                    </a>
                                </TimelineItem>
                                <TimelineItem>
                                    <Icon type="md-cash" color="#19be6b" slot="dot"></Icon>
                                    <a href="https://www.microrule.com/product/risklist.html" target="_blank">信贷系统
                                                    </a>
                                </TimelineItem>

                            </Timeline>
                        </div>
                    </Card>
                    </Col>
                </Row>
                </Col>
                <Col :md="24" :lg="16">
                    <Row :gutter="5">
                        <Col :xs="24" :sm="12" :md="6" :style="{marginBottom: '10px'}">
                        <infor-card id-name="user_created_count" :end-val="count.createUser" iconType="md-person-add" color="#2d8cf0" intro-text="今日新增用户"></infor-card>
                        </Col>
                        <Col :xs="24" :sm="12" :md="6" :style="{marginBottom: '10px'}">
                        <infor-card id-name="visit_count" :end-val="count.visit" iconType="ios-eye" color="#64d572" :iconSize="50" intro-text="今日浏览量"></infor-card>
                        </Col>
                        <Col :xs="24" :sm="12" :md="6" :style="{marginBottom: '10px'}">
                        <infor-card id-name="collection_count" :end-val="count.collection" iconType="md-cloud-upload" color="#ffd572" intro-text="今日数据采集量"></infor-card>
                        </Col>
                        <Col :xs="24" :sm="12" :md="6" :style="{marginBottom: '10px'}">
                        <infor-card id-name="transfer_count" :end-val="count.transfer" iconType="md-shuffle" color="#f25e43" intro-text="今日服务调用量"></infor-card>
                        </Col>
                    </Row>
                    <Row>
                        <Col :md="24" :lg="12" style="padding: 0 5px 10px 0;">
                            <Card>
                                <p slot="title" class="card-title" style="overflow:visible">
                                    <a href="https://www.microrule.com/" target="_blank">
                                        <Icon type="ios-star" size="20" style="margin-bottom:2px"></Icon>
                                        <Badge dot>获取产品</Badge>
                                    </a>
                                </p>
                                <div style="height:368px;overflow:auto">
                                    <div class="qr">
                                        <div>microrule<br>联系
                                            <a href="https://www.microrule.com/" target="_blank">官网</a>
                                        </div>
                                    </div>
                                </div>
                            </Card>
                        </Col>
                        <Col :md="24" :lg="12" style="padding: 0 5px 10px 0;">
                            <Card>
                                <p slot="title" class="card-title">
                                    <Icon type="md-bookmark"></Icon>更新日志
                                </p>
                                <div style="height:368px;overflow:auto">
                                <li>新增ABackTop组件，支持自定义指定滚动容器</li>
                                    <li>新增clipboard全局指令，实现一键复制 /li>
                                    <li> 添加config/index.js配置项，可配置显示在标签栏的基础名称/li>
                                    <li>优化api请求的错误收集 /li>
                                    <li>修复富文本编辑器由于内部z-index设置5过高，遮住iview模态框的问题 /li>
                                    <li>修复多级嵌套路由页面缓存不起作用的bug /li>
                                    <li>更新tree-table-vue版本，修改了个别api名称 </li>
                                </div>
                            </Card>
                        </Col>
                    </Row>
                </Col>
            </Row>
            <Row :gutter="10">
                <Col :md="24" :lg="8" :style="{marginBottom: '10px'}">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="md-map"></Icon>
                        每日来访量统计
                    </p>
                    <div class="data-source-row">
                        <visite-volume></visite-volume>
                    </div>
                </Card>
                </Col>
                <Col :md="24" :lg="16" :style="{marginBottom: '10px'}">
                    <Card :padding="0">
                        <p slot="title" class="card-title">
                            <Icon type="md-locate"></Icon>
                            今日服务调用地理分布
                        </p>
                        <div class="map-con">
                            <Col span="10">
                            <map-data-table :cityData="cityData" height="281" :style-obj="{margin: '12px 0 0 11px'}"></map-data-table>
                            </Col>
                            <Col span="14" class="map-incon">
                            <Row type="flex" justify="center" align="middle">
                                <home-map :city-data="cityData"></home-map>
                            </Row>
                            </Col>
                        </div>
                    </Card>
                </col>
            </Row>
        </div>
        <div v-show="currNav=='doc'||currNav=='xboot-show'||currNav=='xpay'||currNav=='xmall'">
            <show/>
        </div>
        <div v-show="currNav=='app'">
            <h1>Mr Flutter App的首页，点击左侧菜单查看详情</h1>
        </div>

    </div>
</template>

<script>
import { ipInfo } from "@/api/index";
import cityData from "./map-data/get-city-value.js";
import homeMap from "./components/map.vue";
import visiteVolume from "./components/visiteVolume.vue";
import userFlow from "./components/userFlow.vue";
import countUp from "./components/countUp.vue";
import inforCard from "./components/inforCard.vue";
import mapDataTable from "./components/mapDataTable.vue";
import show from "./show.vue";
import Cookies from "js-cookie";
import "gitalk/dist/gitalk.css";
import Gitalk from "gitalk";

export default {
  name: "home",
  components: {
    homeMap,
    visiteVolume,
    userFlow,
    countUp,
    inforCard,
    mapDataTable,
    show
  },
  data() {
    return {
      showVideo: false,
      count: {
        createUser: 496,
        visit: 3264,
        collection: 24389305,
        transfer: 39503498
      },
      cityData: cityData,
      newToDoItemValue: "",
      city: "",
      weather: "",
      username: ""
    };
  },
  computed: {
    currNav() {
      return this.$store.state.app.currNav;
    },
    avatarPath() {
      return localStorage.avatorImgPath;
    }
  },
  methods: {
    init() {
      let userInfo = JSON.parse(Cookies.get("userInfo"));
      this.username = userInfo.username;
      ipInfo().then(res => {
        if (res.success) {
          if(!res.result){
              return
          }
          let ipInfo = JSON.parse(res.result);
          if (ipInfo.retCode == "200") {
            let info = ipInfo.result[0];
            let weather =
              info.weather +
              " " +
              info.temperature +
              " 污染指数: " +
              info.pollutionIndex;
            this.city = info.city;
            this.weather = weather;
          } else {
            this.city = "未知";
            this.weather = "未知";
          }
        }
      });
    }
  },
  mounted() {
    this.init();
  }
};
</script>
