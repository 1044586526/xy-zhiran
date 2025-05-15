<template>
	<view class="content">
		<!-- 灾情通报与救助banner图 -->
		<view class="banner-container">
			<swiper class="banner-swiper" circular indicator-dots indicator-color="rgba(255,255,255,0.4)" 
				indicator-active-color="#ffffff" autoplay interval="3800" duration="500">
				<swiper-item v-for="(item, index) in bannerList" :key="index">
					<view class="banner-item">
						<image class="banner-img" :src="item.bannerImg" mode="aspectFill"></image>
						<view class="banner-info">
							<view class="banner-title">{{item.title}}</view>
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>
		
		<!-- 紧急通知公告 -->
		<view class="notice-container" @click="noticeMore">
			<view class="notice-icon">
				<text class="cuIcon-notificationfill"></text>
			</view>
			<view class="notice-content">
				<swiper class="notice-swiper" vertical circular autoplay interval="4000">
					<swiper-item v-for="(item, index) in messageData" :key="index">
						<view class="notice-item">
							<text class="notice-tag">紧急</text>
							<text class="notice-text">{{item.noticeTitle}}</text>
						</view>
					</swiper-item>
				</swiper>
			</view>
			<view class="notice-more">
				<text class="cuIcon-right"></text>
			</view>
		</view>
		
		<!-- 核心功能区 -->
		<view class="core-function-container">
			<view class="function-item relief" @click="goToDisasterReport">
				<view class="function-icon-box">
					<text class="cuIcon-edit"></text>
				</view>
				<view class="function-info">
					<text class="function-title">救助申报</text>
					<text class="function-desc">填写灾情、损失、资金需求，上传佐证</text>
				</view>
			</view>
			
			<view class="function-item query" @click="goToApplicationQuery">
				<view class="function-icon-box">
					<text class="cuIcon-search"></text>
				</view>
				<view class="function-info">
					<text class="function-title">申报查询</text>
					<text class="function-desc">查看申请记录及处理状态</text>
				</view>
			</view>
			
			<view class="function-item public" @click="goToPublicQuery">
				<view class="function-icon-box">
					<text class="cuIcon-list"></text>
				</view>
				<view class="function-info">
					<text class="function-title">公示查询</text>
					<text class="function-desc">查看已公示的救助信息</text>
				</view>
			</view>
		</view>
		
		<!-- 灾情概览 -->
		<view class="section-header">
			<view class="section-title">
				<text class="title-icon"></text>
				<text class="title-text">灾情概览</text>
			</view>
			<view class="section-more" @click="goDisasterMap">
				<text>灾情地图</text>
				<text class="cuIcon-right"></text>
			</view>
		</view>
		
		<!-- 灾情信息滚动区 -->
		<view class="disaster-scroll-container">
			<scroll-view scroll-x class="disaster-scroll" show-scrollbar="false" enhanced>
				<view class="disaster-card" v-for="(item, index) in disasterInfo" :key="index" 
					:class="'disaster-type-' + (index % 4 + 1)" @click="viewDisasterDetail(item.id)">
					<view class="disaster-tag">{{item.disasterType}}</view>
					<view class="disaster-content">{{item.description}}</view>
					<view class="disaster-action">
						<text>查看详情</text>
						<text class="cuIcon-right"></text>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 救助政策 -->
		<view class="section-header">
			<view class="section-title">
				<text class="title-icon"></text>
				<text class="title-text">救助政策</text>
			</view>
			<view class="section-more" @click="goMorePolicies">
				<text>更多</text>
				<text class="cuIcon-right"></text>
			</view>
		</view>
		
		<!-- 救助政策列表 -->
		<view class="policy-list">
			<view class="policy-card" v-for="(item, index) in policyList" :key="index" @click="viewPolicy(item.id)">
				<image class="policy-image" :src="item.coverImg" mode="aspectFill"></image>
				<view class="policy-info">
					<text class="policy-type">{{item.policyType}}</text>
					<text class="policy-name">{{item.policyName}}</text>
					<text class="policy-summary">{{item.summary}}</text>
					<view class="policy-meta">
						<text class="policy-date">{{item.publishDate}}</text>
						<text class="policy-action">查看详情</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 快速通道 -->
		<view class="quick-access">
			<view class="quick-grid">
				<view class="quick-item" @click="goToEmergencyCall">
					<view class="quick-icon emergency">
						<text class="cuIcon-phone"></text>
					</view>
					<text class="quick-text">紧急求助</text>
				</view>
				<view class="quick-item" @click="goToVolunteerRegister">
					<view class="quick-icon volunteer">
						<text class="cuIcon-friendadd"></text>
					</view>
					<text class="quick-text">志愿服务</text>
				</view>
				<view class="quick-item" @click="goToDonation">
					<view class="quick-icon donation">
						<text class="cuIcon-coin"></text>
					</view>
					<text class="quick-text">爱心捐赠</text>
				</view>
				<view class="quick-item" @click="goToPreventionGuide">
					<view class="quick-icon guide">
						<text class="cuIcon-formfill"></text>
					</view>
					<text class="quick-text">防灾指南</text>
				</view>
			</view>
		</view>
		
		<!-- 页脚 -->
		<view class="footer">
			<view class="hotline">
				<text class="cuIcon-service"></text>
				<text>救灾热线: 12345</text>
			</view>
			<text class="copyright">灾情救助平台 © 2023 版权所有</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				bannerList: [
					{
						bannerImg: 'https://img.cnwest.com/a/10001/202405/10/dc628f70ae358d25a42ba97cf0e527e4.jpeg',
						title: '洪涝灾害救援行动正在进行'
					},
					{
						bannerImg: 'https://ts1.tc.mm.bing.net/th/id/R-C.a26cfbf6bd79c2d667dfe936b3fc6bb8?rik=4Hi1Z6hOQT3WuQ&riu=http%3a%2f%2fpic1.nmgnews.com.cn%2f0%2f11%2f29%2f29%2f11292965_297558.jpg&ehk=i7EEwSDeBMCz2SKUcWyPlqH4aiRRxJFIoZTtGLXiNNI%3d&risl=&pid=ImgRaw&r=0',
						title: '地震灾区救助资金已发放'
					},
					{
						bannerImg: 'https://ts1.tc.mm.bing.net/th/id/R-C.7e29ac6ccff49a7c3390ce5ced6d474e?rik=tJs83omhquSaBg&riu=http%3a%2f%2fpic.weather.com.cn%2fimages%2fcn%2fphoto%2f2020%2f03%2f12%2fs202003121616422E8C4F530D3983027947EF3B5C242BFC.jpg&ehk=%2b8EdeL6zDpNt3pSyoz2NS%2ffC16sHzfMyy3Oqm5FwpCQ%3d&risl=&pid=ImgRaw&r=0',
						title: '旱灾地区供水保障工作启动'
					}
				],
				messageData: [
					{
						noticeTitle: '南方洪水预警，请注意防范！',
						noticeDesc: '南方多地进入暴雨季节，请做好防汛准备'
					},
					{
						noticeTitle: '西北地区干旱灾情通报',
						noticeDesc: '西北地区持续干旱，受灾群众可申请救助'
					},
					{
						noticeTitle: '山区泥石流灾害预警发布',
						noticeDesc: '近期山区降雨量大，注意防范泥石流灾害'
					}
				],
				disasterInfo: [
					{
						id: 1,
						disasterType: '洪涝灾害',
						description: '南方地区严重洪涝，多个村庄受灾，已启动应急救援预案...'
					},
					{
						id: 2,
						disasterType: '地震灾害',
						description: '西部地区发生5.2级地震，已转移安置受灾群众2000余人...'
					},
					{
						id: 3,
						disasterType: '干旱灾害',
						description: '北方地区持续干旱，农作物受灾面积超过50万亩，紧急调配救灾物资...'
					},
					{
						id: 4,
						disasterType: '台风灾害',
						description: '东南沿海地区台风"海棠"来袭，已启动防台风一级响应...'
					}
				],
				policyList: [
					{
						id: 1,
						coverImg: 'https://img.cnwest.com/a/10001/202405/10/dc628f70ae358d25a42ba97cf0e527e4.jpeg',
						policyType: '救助政策',
						policyName: '自然灾害生活救助实施办法',
						summary: '为规范自然灾害生活救助工作，推动救助政策精准落实...',
						publishDate: '2023-07-15'
					},
					{
						id: 2,
						coverImg: 'https://img.cnwest.com/a/10001/202405/10/dc628f70ae358d25a42ba97cf0e527e4.jpeg',
						policyType: '资金申请',
						policyName: '受灾群众救助资金申请指南',
						summary: '受灾群众可通过本平台或当地民政部门申请救助资金...',
						publishDate: '2023-06-20'
					}
				]
			}
		},
		onLoad() {
			// 数据加载，保留必要的请求
		},
		methods: {
			// 跳转到救助申报页面
			goToDisasterReport() {
				try {
					// 添加错误捕获，并采用另一种路径格式
					uni.navigateTo({
						url: '../disaster/report',
						success: function() {
							console.log('跳转成功');
						},
						fail: function(err) {
							console.error('跳转失败', err);
							// 尝试提示错误
							uni.showToast({
								title: '页面跳转失败，请检查页面路径',
								icon: 'none'
							});
						}
					});
				} catch(e) {
					console.error('跳转异常', e);
				}
			},
			
			// 跳转到申报查询页面
			goToApplicationQuery() {
				try {
					uni.navigateTo({
						url: '../disaster/query',
						success: function() {
							console.log('跳转成功');
						},
						fail: function(err) {
							console.error('跳转失败', err);
							uni.showToast({
								title: '页面跳转失败，请检查页面路径',
								icon: 'none'
							});
						}
					});
				} catch(e) {
					console.error('跳转异常', e);
				}
			},
			
			// 跳转到公示查询页面
			goToPublicQuery() {
				try {
					uni.navigateTo({
						url: '../disaster/public-query',
						success: function() {
							console.log('跳转成功');
						},
						fail: function(err) {
							console.error('跳转失败', err);
							uni.showToast({
								title: '页面跳转失败，请检查页面路径',
								icon: 'none'
							});
						}
					});
				} catch(e) {
					console.error('跳转异常', e);
				}
			},
			
			// 查看更多通知
			noticeMore() {
				uni.navigateTo({
					url: '../notice/notice-list'
				});
			},
			
			// 跳转到灾情地图
			goDisasterMap() {
				uni.navigateTo({
					url: '../disaster/disaster-map'
				});
			},
			
			// 查看灾情详情
			viewDisasterDetail(id) {
				uni.navigateTo({
					url: `../disaster/disaster-detail?id=${id}`
				});
			},
			
			// 查看更多政策
			goMorePolicies() {
				uni.navigateTo({
					url: '../policy/policy-list'
				});
			},
			
			// 查看政策详情
			viewPolicy(id) {
				uni.navigateTo({
					url: `../policy/policy-detail?id=${id}`
				});
			},
			
			// 紧急求助
			goToEmergencyCall() {
				uni.navigateTo({
					url: '../emergency/emergency-call'
				});
			},
			
			// 志愿者注册
			goToVolunteerRegister() {
				uni.navigateTo({
					url: '../volunteer/register'
				});
			},
			
			// 爱心捐赠
			goToDonation() {
				uni.navigateTo({
					url: '../donation/donation'
				});
			},
			
			// 防灾指南
			goToPreventionGuide() {
				uni.navigateTo({
					url: '../guide/prevention-guide'
				});
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f7f8fa;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
	}
	
	.content {
		padding-bottom: 40rpx;
	}
	
	/* Banner样式 */
	.banner-container {
		width: 100%;
		height: 360rpx;
		position: relative;
	}
	
	.banner-swiper {
		width: 100%;
		height: 100%;
	}
	
	.banner-item {
		width: 100%;
		height: 100%;
		position: relative;
	}
	
	.banner-img {
		width: 100%;
		height: 100%;
	}
	
	.banner-info {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
		padding: 30rpx 40rpx;
	}
	
	.banner-title {
		color: #fff;
		font-size: 32rpx;
		font-weight: bold;
	}
	
	/* 通知公告样式 */
	.notice-container {
		margin: 30rpx 30rpx 0;
		height: 90rpx;
		background: #fff;
		border-radius: 45rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.08);
	}
	
	.notice-icon {
		width: 90rpx;
		height: 90rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		color: #ff6b6b;
		font-size: 40rpx;
	}
	
	.notice-content {
		flex: 1;
		height: 90rpx;
		overflow: hidden;
	}
	
	.notice-swiper {
		height: 90rpx;
	}
	
	.notice-item {
		display: flex;
		align-items: center;
		height: 90rpx;
	}
	
	.notice-tag {
		background-color: #ff6b6b;
		color: #fff;
		font-size: 22rpx;
		padding: 4rpx 12rpx;
		border-radius: 6rpx;
		margin-right: 16rpx;
	}
	
	.notice-text {
		font-size: 28rpx;
		color: #333;
		flex: 1;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	
	.notice-more {
		width: 80rpx;
		height: 90rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		color: #999;
		font-size: 24rpx;
	}
	
	/* 核心功能样式 */
	.core-function-container {
		margin: 40rpx 30rpx 0;
	}
	
	.function-item {
		height: 180rpx;
		margin-bottom: 24rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		padding: 0 40rpx;
		background: #fff;
		box-shadow: 0 6rpx 16rpx rgba(0,0,0,0.06);
		position: relative;
		overflow: hidden;
	}
	
	.function-item::after {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		width: 12rpx;
		height: 100%;
	}
	
	.function-item.relief::after {
		background: linear-gradient(to bottom, #ff6b6b, #ff8e8e);
	}
	
	.function-item.query::after {
		background: linear-gradient(to bottom, #4facfe, #00f2fe);
	}
	
	.function-item.public::after {
		background: linear-gradient(to bottom, #0acf83, #00e778);
	}
	
	.function-icon-box {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 50rpx;
		margin-right: 30rpx;
	}
	
	.function-item.relief .function-icon-box {
		background: rgba(255, 107, 107, 0.1);
		color: #ff6b6b;
	}
	
	.function-item.query .function-icon-box {
		background: rgba(79, 172, 254, 0.1);
		color: #4facfe;
	}
	
	.function-item.public .function-icon-box {
		background: rgba(10, 207, 131, 0.1);
		color: #0acf83;
	}
	
	.function-info {
		flex: 1;
	}
	
	.function-title {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
		display: block;
	}
	
	.function-desc {
		font-size: 26rpx;
		color: #999;
		display: block;
	}
	
	/* 模块标题样式 */
	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 50rpx 30rpx 24rpx;
	}
	
	.section-title {
		display: flex;
		align-items: center;
	}
	
	.title-icon {
		width: 8rpx;
		height: 36rpx;
		background: #4facfe;
		margin-right: 16rpx;
		border-radius: 4rpx;
	}
	
	.title-text {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
	}
	
	.section-more {
		font-size: 26rpx;
		color: #999;
		display: flex;
		align-items: center;
	}
	
	.section-more .cuIcon-right {
		font-size: 24rpx;
		margin-left: 6rpx;
	}
	
	/* 灾情信息滚动样式 */
	.disaster-scroll-container {
		margin: 0 30rpx;
	}
	
	.disaster-scroll {
		white-space: nowrap;
	}
	
	.disaster-card {
		display: inline-block;
		width: 300rpx;
		height: 320rpx;
		margin-right: 20rpx;
		border-radius: 20rpx;
		background: #fff;
		padding: 30rpx;
		position: relative;
		box-shadow: 0 6rpx 16rpx rgba(0,0,0,0.04);
	}
	
	.disaster-tag {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 16rpx;
	}
	
	.disaster-type-1 .disaster-tag {
		color: #4facfe;
	}
	
	.disaster-type-2 .disaster-tag {
		color: #ff6b6b;
	}
	
	.disaster-type-3 .disaster-tag {
		color: #ff9f43;
	}
	
	.disaster-type-4 .disaster-tag {
		color: #0acf83;
	}
	
	.disaster-content {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		height: 180rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 5;
		-webkit-box-orient: vertical;
		white-space: normal;
	}
	
	.disaster-action {
		position: absolute;
		bottom: 30rpx;
		left: 30rpx;
		font-size: 24rpx;
		display: flex;
		align-items: center;
	}
	
	.disaster-type-1 .disaster-action {
		color: #4facfe;
	}
	
	.disaster-type-2 .disaster-action {
		color: #ff6b6b;
	}
	
	.disaster-type-3 .disaster-action {
		color: #ff9f43;
	}
	
	.disaster-type-4 .disaster-action {
		color: #0acf83;
	}
	
	.disaster-action .cuIcon-right {
		font-size: 22rpx;
		margin-left: 6rpx;
	}
	
	/* 政策列表样式 */
	.policy-list {
		margin: 0 30rpx;
	}
	
	.policy-card {
		background: #fff;
		border-radius: 20rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 6rpx 16rpx rgba(0,0,0,0.04);
		overflow: hidden;
	}
	
	.policy-image {
		width: 100%;
		height: 240rpx;
	}
	
	.policy-info {
		padding: 30rpx;
	}
	
	.policy-type {
		display: inline-block;
		font-size: 24rpx;
		color: #fff;
		padding: 6rpx 16rpx;
		border-radius: 100rpx;
		background: linear-gradient(to right, #ff6b6b, #ff8e8e);
		margin-bottom: 16rpx;
	}
	
	.policy-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
		display: block;
	}
	
	.policy-summary {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 20rpx;
		display: block;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
	
	.policy-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	
	.policy-date {
		font-size: 24rpx;
		color: #999;
	}
	
	.policy-action {
		font-size: 26rpx;
		color: #4facfe;
	}
	
	/* 快速通道 */
	.quick-access {
		margin: 50rpx 30rpx 0;
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 6rpx 16rpx rgba(0,0,0,0.04);
	}
	
	.quick-grid {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
	}
	
	.quick-item {
		width: 48%;
		height: 160rpx;
		margin-bottom: 20rpx;
		background: #f8f9fa;
		border-radius: 16rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.quick-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 16rpx;
		font-size: 40rpx;
		color: #fff;
	}
	
	.quick-icon.emergency {
		background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
	}
	
	.quick-icon.volunteer {
		background: linear-gradient(135deg, #4facfe, #00f2fe);
	}
	
	.quick-icon.donation {
		background: linear-gradient(135deg, #ff9f43, #ffc93c);
	}
	
	.quick-icon.guide {
		background: linear-gradient(135deg, #0acf83, #00e778);
	}
	
	.quick-text {
		font-size: 28rpx;
		color: #666;
	}
	
	/* 页脚 */
	.footer {
		margin-top: 60rpx;
		padding: 40rpx 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.hotline {
		display: flex;
		align-items: center;
		background: rgba(79, 172, 254, 0.1);
		padding: 16rpx 30rpx;
		border-radius: 100rpx;
		margin-bottom: 20rpx;
	}
	
	.hotline text {
		color: #4facfe;
		font-size: 28rpx;
		font-weight: bold;
	}
	
	.hotline .cuIcon-service {
		margin-right: 10rpx;
		font-size: 32rpx;
	}
	
	.copyright {
		font-size: 24rpx;
		color: #999;
	}
</style>
