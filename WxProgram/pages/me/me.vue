<template>
	<view class="profile-container">
		<!-- 顶部背景与个人信息卡片 -->
		<view class="header-section">
			<view class="background-gradient">
				<!-- <image class="bg-pattern" src="../../static/pattern.png" mode="scaleToFill"></image> -->
			</view>
			
			<view class="profile-card" @click="goToLogin">
				<view v-if="islogin" class="user-info">
					<view class="avatar-container">
						<image class="avatar" :src="userinfo.avatar" mode="aspectFill"></image>
						<view class="avatar-badge"></view>
					</view>
					
					<view class="user-details">
						<view class="user-name">{{ userinfo.nickname || '未设置昵称' }}</view>
						<view class="user-id">ID: {{ userinfo.id }}</view>
						<view class="vip-tag" :class="getRoleClass()">{{ getRoleText() }}</view>
					</view>
					
					<view class="edit-profile">
						<uni-icons type="forward" size="16" color="#999"></uni-icons>
					</view>
				</view>
				
				<view v-else class="login-prompt">
					<view class="avatar-container">
						<image class="avatar" :src="avatarUrl" mode="aspectFill"></image>
					</view>
					
					<view class="login-text">
						<text>点击登录账号</text>
						<text class="login-desc">登录后体验更多功能</text>
					</view>
					
					<view class="login-arrow">
						<uni-icons type="forward" size="16" color="#999"></uni-icons>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 数据统计卡片 -->
		<!-- <view class="stats-card">
			<view class="stat-item">
				<text class="stat-number">0</text>
				<text class="stat-label">我的申报</text>
			</view>
			<view class="stat-divider"></view>
			<view class="stat-item">
				<text class="stat-number">0</text>
				<text class="stat-label">已处理</text>
			</view>
			<view class="stat-divider"></view>
			<view class="stat-item">
				<text class="stat-number">0</text>
				<text class="stat-label">待审核</text>
			</view>
		</view> -->
		
		<!-- 菜单列表 -->
		<view class="menu-section">
			<!-- 功能菜单组 -->
			<view class="menu-group">
				<view class="menu-item" @click="goToMyApplications">
					<view class="menu-icon blue">
						<uni-icons type="file-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">我的申报</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<view class="menu-item" v-if="isReviewer" @click="goToEvaluation">
					<view class="menu-icon purple">
						<uni-icons type="star-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">民主评议</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<view class="menu-item" v-if="isAdmin" @click="goToAdminPanel">
					<view class="menu-icon red">
						<uni-icons type="gear-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">管理后台</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<view class="menu-item" @click="goToApprovalStatus">
					<view class="menu-icon green">
						<uni-icons type="flag-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">审批状态</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<!-- <view class="menu-item" @click="goToMessages">
					<view class="menu-icon orange">
						<uni-icons type="notification-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">消息通知</view>
					<view class="menu-badge" v-if="unreadMessages > 0">{{unreadMessages}}</view>
					<view class="menu-arrow" v-else>
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view> -->
			</view>
			
			<!-- 支持与帮助菜单组 -->
			<view class="menu-group">
				<view class="menu-item" @click="goToHelp(0)">
					<view class="menu-icon purple">
						<uni-icons type="help-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">帮助中心</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<view class="menu-item" @click="goToHelp(2)">
					<view class="menu-icon red">
						<uni-icons type="chat-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">意见反馈</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
				
				<view class="menu-item" @click="goToAbout">
					<view class="menu-icon teal">
						<uni-icons type="info-filled" size="24" color="#ffffff"></uni-icons>
					</view>
					<view class="menu-text">关于我们</view>
					<view class="menu-arrow">
						<uni-icons type="right" size="16" color="#ccc"></uni-icons>
					</view>
				</view>
			</view>
			
			<!-- 退出登录按钮 -->
			<view class="logout-button" v-if="islogin" @click="loginOut">
				<text>退出登录</text>
			</view>
		</view>
		
		<!-- 底部版权信息 -->
		<view class="footer">
			<text class="copyright">灾情救助平台 © 2023 版权所有</text>
		</view>
	</view>
</template>

<script>
	import {
		setUserInfo,
		getUserInfo,
		setToken,
		getToken,
		removeUserInfo,
		removeToken
	} from '@/utils/auth';
	
	export default {
		data() {
			return {
				avatarUrl: 'https://portrait.gitee.com/uploads/avatars/user/354/1062657_rahman_1649233036.png!avatar60',
				userinfo: {},
				islogin: false,
				unreadMessages: 2, // 未读消息数量
				userRole: 'user', // 默认为普通用户
			}
		},
		computed: {
			// 判断是否为管理员
			isAdmin() {
				return this.userRole === 'admin';
			},
			// 判断是否为评议员
			isReviewer() {
				return this.userRole === 'reviewer';
			},
			// 判断是否为普通用户
			isUser() {
				return this.userRole === 'user';
			}
		},
		onShow() {
			console.log(getUserInfo())
			if(getUserInfo() != undefined) {
				this.islogin = true;
				this.userinfo = getUserInfo() || {};
				
				// 获取用户角色
				this.getUserRole();
			} else {
				this.userinfo = {};
			}
		},
		methods: {
			// 获取用户角色信息
			getUserRole() {
				try {
					// 首先尝试从本地缓存读取完整的用户信息
					const localUserInfo = uni.getStorageSync('user_info');
					
					// 检查本地存储的user_info中是否有userrole字段
					if (localUserInfo && (localUserInfo.userrole || localUserInfo.role)) {
						const roleFromStorage = localUserInfo.userrole || localUserInfo.role;
						console.log('从本地缓存获取用户角色:', roleFromStorage);
						
						// 设置用户角色
						this.userRole = roleFromStorage.toLowerCase();
						
						// 更新userinfo中的role字段
						if (!this.userinfo.role) {
							const updatedUserInfo = { ...this.userinfo, role: this.userRole };
							setUserInfo(updatedUserInfo);
							this.userinfo = updatedUserInfo;
						}
						
						return; // 已获取到角色，直接返回，不发送请求
					}
					
					// 如果userinfo中已有role信息，也直接使用
					if (this.userinfo.role) {
						this.userRole = this.userinfo.role.toLowerCase();
						console.log('从userInfo获取用户角色:', this.userRole);
						return;
					}
					
					// 如果本地没有角色信息，再尝试从服务器获取
					const baseUrl = uni.getStorageSync('baseUrl') || '';
					if (!baseUrl) {
						console.log('未设置baseUrl，无法获取用户角色');
						this.userRole = 'user'; // 默认为普通用户
						return;
					}
					
					// 为防止404错误，先打个日志
					console.log('准备从服务器获取用户角色，URL:', `${baseUrl}/wxapi/user/role`);
					
					// uni.request({
					// 	url: `${baseUrl}/wxapi/user/role`,
					// 	method: 'GET',
					// 	header: {
					// 		'Authorization': getToken() || ''
					// 	},
					// 	success: (res) => {
					// 		if (res.statusCode === 200 && res.data.code === 200) {
					// 			// 保存用户角色
					// 			this.userRole = res.data.data.role || 'user';
								
					// 			// 更新用户信息
					// 			const updatedUserInfo = { ...this.userinfo, role: this.userRole };
					// 			setUserInfo(updatedUserInfo);
					// 			this.userinfo = updatedUserInfo;
								
					// 			console.log('从服务器获取用户角色:', this.userRole);
					// 		} else {
					// 			console.error('获取用户角色失败:', res.data);
					// 			this.userRole = 'user'; // 默认为普通用户
					// 		}
					// 	},
					// 	fail: (err) => {
					// 		console.error('获取用户角色请求失败:', err);
					// 		this.userRole = 'user'; // 默认为普通用户
					// 	}
					// });
				} catch (error) {
					console.error('获取用户角色出错:', error);
					this.userRole = 'user'; // 默认为普通用户
				}
			},
			
			// 获取角色显示文本
			getRoleText() {
				switch (this.userRole) {
					case 'admin':
						return '系统管理员';
					case 'reviewer':
						return '评议员';
					case 'user':
					default:
						return '普通用户';
				}
			},
			
			// 获取角色标签样式类
			getRoleClass() {
				switch (this.userRole) {
					case 'admin':
						return 'admin-tag';
					case 'reviewer':
						return 'reviewer-tag';
					case 'user':
					default:
						return 'user-tag';
				}
			},
			
			// 跳转到民主评议页面（评议员专用）
			goToEvaluation() {
				if (!this.islogin) {
					this.promptLogin();
					return;
				}
				
				if (!this.isReviewer) {
					uni.showToast({
						title: '您没有评议员权限',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				uni.navigateTo({
					url: '/pages/admin/shenpiList',
					success: (res) => {
						console.log('跳转到民主评议页面成功');
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			
			// 跳转到管理后台（管理员专用）
			goToAdminPanel() {
				if (!this.islogin) {
					this.promptLogin();
					return;
				}
				
				if (!this.isAdmin) {
					uni.showToast({
						title: '您没有管理员权限',
						icon: 'none',
						duration: 2000
					});
					return;
				}
				
				uni.navigateTo({
					// url: '/pages/admin/shenpiList',
					 url: '/pages/admin/admin',
					success: (res) => {
						console.log('跳转到管理后台成功');
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			
			goToLogin() {
				if (!this.islogin) {
					uni.navigateTo({
						url: '/pages/login/login',
						success: (res) => {
							console.log('跳转到登录页成功');
						},
						fail: (err) => {
							console.error('跳转到登录页失败:', err);
							uni.showToast({
								title: '跳转失败，请稍后重试',
								icon: 'none',
								duration: 2000
							});
						}
					});
				} else {
					uni.navigateTo({
						url: '/pages/me/userinfo'
					});
				}
			},
			
			goToMyApplications() {
				// 检查登录状态
				if (!this.islogin) {
					this.promptLogin();
					return;
				}
				
				// 修改为跳转到申报查询页面
				uni.navigateTo({
					url: '/pages/disaster/query',
					success: (res) => {
						console.log('跳转到申报查询页面成功');
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			
			goToApprovalStatus() {
				// 检查登录状态
				if (!this.islogin) {
					this.promptLogin();
					return;
				}
				
				// 也跳转到申报查询页面，与"我的申报"相同
				uni.navigateTo({
					url: '/pages/disaster/query',
					success: (res) => {
						console.log('跳转到申报查询页面成功');
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			
			goToMessages() {
				// 检查登录状态
				if (!this.islogin) {
					this.promptLogin();
					return;
				}
				
				uni.navigateTo({
					url: '/pages/me/messages'
				});
			},
			
			goToAbout() {
				uni.navigateTo({
					url: '/pages/me/about-us'
				});
			},
			
			promptLogin() {
				uni.showModal({
					title: '温馨提示',
					content: '该功能需要登录后使用，是否前往登录？',
					confirmText: '去登录',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							this.goToLogin();
						}
					}
				});
			},
			
			goToHelp(tabIndex) {
				console.log('跳转到帮助中心，标签页:', tabIndex);
				
				uni.reLaunch({
					url: '/pages/about/about?tab=' + tabIndex,
					success: (res) => {
						console.log('跳转成功:', res);
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			
			loginOut() {
				uni.showModal({
					title: '退出登录',
					content: '确定要退出当前账号吗？',
					cancelText: "取消", 
					confirmText: "确定", 
					confirmColor: '#f55850',
					success: (res) => {
						if (res.confirm) {
							uni.showToast({
								title: "退出成功",
								icon: "success",
								duration: 2000
							});
							removeUserInfo();
							removeToken();
							this.islogin = false;
						}
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f7fa;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
	}
	
	.profile-container {
		min-height: 100vh;
		display: flex;
		flex-direction: column;
	}
	
	/* 顶部背景与个人信息卡片 */
	.header-section {
		position: relative;
		padding-bottom: 30rpx;
	}
	
	.background-gradient {
		height: 360rpx;
		background: linear-gradient(120deg, #1677FF, #30A0FF);
		border-bottom-left-radius: 30rpx;
		border-bottom-right-radius: 30rpx;
		position: relative;
		overflow: hidden;
	}
	
	.bg-pattern {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		opacity: 0.1;
	}
	
	.profile-card {
		width: 90%;
		margin: -180rpx auto 0;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.08);
		padding: 40rpx;
		position: relative;
		z-index: 2;
	}
	
	.user-info, .login-prompt {
		display: flex;
		align-items: center;
	}
	
	.avatar-container {
		position: relative;
		margin-right: 30rpx;
	}
	
	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 4rpx solid #ffffff;
		box-shadow: 0 6rpx 15rpx rgba(0, 0, 0, 0.1);
	}
	
	.avatar-badge {
		position: absolute;
		right: 0;
		bottom: 6rpx;
		width: 30rpx;
		height: 30rpx;
		border-radius: 50%;
		background-color: #52C41A;
		border: 4rpx solid #ffffff;
		z-index: 3;
	}
	
	.user-details {
		flex: 1;
	}
	
	.user-name {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
	}
	
	.user-id {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 12rpx;
	}
	
	/* 用户角色标签样式 */
	.vip-tag {
		display: inline-block;
		font-size: 22rpx;
		border-radius: 6rpx;
		padding: 4rpx 12rpx;
	}
	
	.user-tag {
		color: #FA8C16;
		background-color: #FFF7E6;
		border: 1rpx solid #FFD591;
	}
	
	.reviewer-tag {
		color: #722ED1;
		background-color: #F9F0FF;
		border: 1rpx solid #D3ADF7;
	}
	
	.admin-tag {
		color: #F5222D;
		background-color: #FFF1F0;
		border: 1rpx solid #FFA39E;
	}
	
	.login-text {
		flex: 1;
		display: flex;
		flex-direction: column;
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.login-desc {
		font-size: 24rpx;
		color: #999;
		font-weight: normal;
		margin-top: 8rpx;
	}
	
	/* 数据统计卡片 */
	.stats-card {
		width: 90%;
		margin: 30rpx auto;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
		padding: 30rpx 0;
		display: flex;
		justify-content: space-around;
	}
	
	.stat-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.stat-divider {
		width: 1rpx;
		height: 60rpx;
		background-color: #f0f0f0;
		align-self: center;
	}
	
	.stat-number {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	
	.stat-label {
		font-size: 24rpx;
		color: #999;
	}
	
	/* 菜单列表 */
	.menu-section {
		flex: 1;
		padding: 0 30rpx;
	}
	
	.menu-group {
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
		margin-bottom: 30rpx;
		overflow: hidden;
	}
	
	.menu-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		position: relative;
	}
	
	.menu-item:not(:last-child)::after {
		content: "";
		position: absolute;
		left: 90rpx;
		right: 0;
		bottom: 0;
		height: 1rpx;
		background-color: #f5f5f5;
	}
	
	.menu-icon {
		width: 60rpx;
		height: 60rpx;
		border-radius: 15rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
	}
	
	.menu-icon.blue {
		background: linear-gradient(135deg, #1677FF, #4DA2FF);
	}
	
	.menu-icon.green {
		background: linear-gradient(135deg, #52C41A, #85E841);
	}
	
	.menu-icon.orange {
		background: linear-gradient(135deg, #FA8C16, #FFC53D);
	}
	
	.menu-icon.purple {
		background: linear-gradient(135deg, #722ED1, #9254DE);
	}
	
	.menu-icon.red {
		background: linear-gradient(135deg, #F5222D, #FF4D4F);
	}
	
	.menu-icon.teal {
		background: linear-gradient(135deg, #13C2C2, #36CFC9);
	}
	
	.menu-text {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}
	
	.menu-arrow {
		color: #ccc;
	}
	
	.menu-badge {
		min-width: 36rpx;
		height: 36rpx;
		border-radius: 18rpx;
		background-color: #F5222D;
		color: #ffffff;
		font-size: 22rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 10rpx;
	}
	
	/* 退出登录按钮 */
	.logout-button {
		margin: 60rpx auto;
		width: 80%;
		height: 90rpx;
		line-height: 90rpx;
		text-align: center;
		background-color: #ffffff;
		color: #F5222D;
		font-size: 30rpx;
		border-radius: 45rpx;
		border: 1rpx solid #F5222D;
	}
	
	.logout-button:active {
		opacity: 0.8;
	}
	
	/* 底部版权信息 */
	.footer {
		padding: 30rpx 0 60rpx;
		text-align: center;
	}
	
	.copyright {
		font-size: 24rpx;
		color: #999;
	}
</style>
