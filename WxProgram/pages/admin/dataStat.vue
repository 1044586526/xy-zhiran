<template>
	<view class="dataStat-wrap">
		<view class="title-wrap">
			<view :class="{'item': true, 'last': index === 2}" v-for="(item,index) in headerList">
				{{item}}
			</view>
		</view>
		<view class="data-wrap" >
			<view class="item-wrap"v-for="(item,index) in data">
				<view class="value">{{item.name}}</view>
				<view class="value">{{item.cardNo}}</view>
				<view :class="{'value': true, 'last-value': true}">{{item.requestAmount}}</view>
			</view>
		</view>
		<view class="total-wrap">总计：{{total}}</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				headerList:["姓名", "银行卡号", "应发放金额(元)" ],
				data: [],
				total: 0
			};
		},
		methods:{
			getData() {
			uni.request({
				url: 'http://localhost:8083/wxapi/list/count',
				method: 'POST', // 改为POST方法
				header: {
					'Content-Type': 'application/json' // 修改为JSON格式
				},
				success: (res) => {
					console.log(res)
					const data = res.data
					this.data = data.list
					this.total = data.count
				},
				fail: (err) => {
					
				}
			});
			}
		},
		created() {
			this.getData()
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f8fa;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
	}
	.dataStat-wrap {
		width: 100%;
		height: 100%;
		padding: 0 30rpx;
		margin-top: 20rpx;
		background-color: #fff;
		.title-wrap {
			width: 100%;
			display: flex;
			margin-bottom: 20rpx;
			justify-content: space-between;
			.item {
				flex: 1;
				&.last {
					text-align: right;
				}
			}
		}
		.data-wrap {
			width: 100%;
			border-bottom: 1rpx solid #ddd;
			.item-wrap  {
				width: 100%;
				display: flex;
				justify-content: space-between;
				margin-bottom: 10rpx;
				.value {
					flex: 1;
					
				}
				.last-value {
					text-align: right;
				}
			}
		}
		.total-wrap {
			width: 100%;
			text-align: right;
		}
	}
</style>
