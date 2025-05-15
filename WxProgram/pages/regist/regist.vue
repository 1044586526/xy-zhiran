<template>
	<view class="regist-container">
		<!-- 背景图片与渐变遮罩 -->
		<!-- <image class="bg-img" src="../../static/bannerlist2.jpg" mode="aspectFill"></image> -->
		<view class="gradient-overlay"></view>
		
		<!-- 全屏注册卡片 -->
		<view class="regist-card">
			<!-- 顶部装饰波浪 -->
			<view class="wave-decoration"></view>
			
			<!-- 返回按钮 -->
			<view class="back-button" @tap="goBack">
				<uni-icons type="back" size="22" color="#fff"></uni-icons>
			</view>
			
			<!-- 头部区域 -->
			<view class="header-section">
				<view class="brand-container">
					<image class="brand-logo" src="../../static/logo.png" mode="aspectFit"></image>
					<view class="brand-text">
						<text class="brand-name">智能管理系统</text>
						<text class="brand-slogan">安全 · 高效 · 专业</text>
					</view>
				</view>
			</view>

			<!-- 主体内容 -->
			<view class="regist-content">
				<view class="regist-header">
					<text class="regist-title">账号注册</text>
					<text class="regist-subtitle">请填写您的真实信息完成注册</text>
				</view>

				<scroll-view scroll-y class="form-scroll-area">
					<view class="regist-form">
						<!-- 真实姓名 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="person" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">真实姓名</view>
							<input 
								v-model="formData.realName" 
								type="text"
								placeholder="请输入您的真实姓名" 
								class="native-input"
							/>
						</view>
						
						<!-- 身份证号 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="idcard" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">身份证号</view>
							<input 
								v-model="formData.idCard" 
								type="idcard"
								placeholder="请输入18位身份证号" 
								class="native-input"
								maxlength="18"
							/>
						</view>
						
						<!-- 户籍所在地 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="location" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">户籍地址</view>
							<input 
								v-model="formData.domicile" 
								type="text"
								placeholder="请输入户籍所在地" 
								class="native-input"
							/>
						</view>
						
						<!-- 联系方式 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="phone-filled" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">联系方式</view>
							<input 
								v-model="formData.contact" 
								type="number"
								placeholder="请输入手机号或固话" 
								class="native-input"
								maxlength="11"
							/>
						</view>
						
						<!-- 开户银行 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="wallet" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">开户银行</view>
							<picker 
								@change="bankPickerChange" 
								:value="bankIndex" 
								:range="bankOptions"
								class="bank-picker"
							>
								<view class="picker-content">
									<text class="picker-text">{{bankIndex > -1 ? bankOptions[bankIndex] : '请选择开户银行'}}</text>
									<uni-icons type="arrowright" size="18" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
						
						<!-- 银行账号 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="vip" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">银行账号</view>
							<input 
								v-model="formData.bankAccount" 
								type="number"
								placeholder="请输入银行卡号" 
								class="native-input"
							/>
						</view>
						
						<!-- 登录密码 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="locked" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">登录密码</view>
							<input 
								v-model="formData.loginPassword" 
								type="password"
								placeholder="请设置登录密码" 
								class="native-input"
								password
							/>
						</view>
						
						<!-- 确认密码 -->
						<view class="form-item">
							<view class="input-icon">
								<uni-icons type="locked-filled" size="24" color="#0056b3"></uni-icons>
							</view>
							<view class="input-label">确认密码</view>
							<input 
								v-model="confirmPassword" 
								type="password"
								placeholder="请再次输入密码" 
								class="native-input"
								password
							/>
						</view>
						
						<!-- 服务协议 -->
						<view class="agreement-row">
							<view class="agreement-option" @tap="agreeTerms = !agreeTerms">
								<view class="custom-checkbox" :class="{'checked': agreeTerms}">
									<uni-icons v-if="agreeTerms" type="checkmarkempty" size="14" color="#ffffff"></uni-icons>
								</view>
								<text class="agreement-text">我已阅读并同意</text>
								<text class="agreement-link">《服务协议》</text>
								<text class="agreement-text">和</text>
								<text class="agreement-link">《隐私政策》</text>
							</view>
						</view>
						
						<!-- 注册按钮 -->
						<button 
							class="regist-button" 
							:loading="loading" 
							:disabled="loading || !agreeTerms"
							:class="{'button-disabled': !agreeTerms}"
							@tap="doRegist"
						>
							{{ loading ? '提交中...' : '立即注册' }}
						</button>
						
						<!-- 已有账号 -->
						<view class="login-link-row">
							<text class="login-tip">已有账号？</text>
							<text class="login-link" @tap="goToLogin">立即登录</text>
						</view>
					</view>
				</scroll-view>
			</view>
			
			<!-- 底部装饰 -->
			<view class="footer-decoration">
				<view class="decoration-dot"></view>
				<view class="decoration-dot"></view>
				<view class="decoration-dot"></view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				agreeTerms: false,
				confirmPassword: '',
				bankIndex: -1,
				bankOptions: [
					'中国工商银行', '中国农业银行', '中国建设银行', 
					'中国银行', '交通银行', '中国邮政储蓄银行',
					'招商银行', '浦发银行', '中信银行', 
					'兴业银行', '光大银行', '华夏银行', 
					'民生银行', '平安银行'
				],
				formData: {
					realName: '',
					idCard: '',
					domicile: '',
					contact: '',
					bankName: '',
					bankAccount: '',
					loginPassword: ''
				}
			}
		},
		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},
			
			// 去登录页
			goToLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			},
			
			// 银行选择变化
			bankPickerChange(e) {
				this.bankIndex = e.detail.value;
				this.formData.bankName = this.bankOptions[this.bankIndex];
			},
			
			// 验证表单
			validateForm() {
				// 姓名验证
				if (!this.formData.realName) {
					this.showToast('请输入真实姓名');
					return false;
				}
				
				// 身份证验证
				const idPattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
				if (!this.formData.idCard) {
					this.showToast('请输入身份证号');
					return false;
				} else if (!idPattern.test(this.formData.idCard)) {
					this.showToast('请输入正确的身份证号');
					return false;
				}
				
				// 户籍地址验证
				if (!this.formData.domicile) {
					this.showToast('请输入户籍所在地');
					return false;
				}
				
				// 联系方式验证
				const phonePattern = /^1[3-9]\d{9}$/;
				const telPattern = /^(0\d{2,3}-\d{7,8})$/;
				if (!this.formData.contact) {
					this.showToast('请输入联系方式');
					return false;
				} else if (!phonePattern.test(this.formData.contact) && !telPattern.test(this.formData.contact)) {
					this.showToast('请输入正确的联系方式');
					return false;
				}
				
				// 银行账号验证
				if (!this.formData.bankAccount) {
					this.showToast('请输入银行账号');
					return false;
				} else if (!/^\d{10,19}$/.test(this.formData.bankAccount)) {
					this.showToast('请输入正确的银行账号');
					return false;
				}
				
				// 密码验证
				if (!this.formData.loginPassword) {
					this.showToast('请设置登录密码');
					return false;
				} else if (this.formData.loginPassword.length < 6) {
					this.showToast('密码长度不能少于6位');
					return false;
				}
				
				// 确认密码
				if (this.formData.loginPassword !== this.confirmPassword) {
					this.showToast('两次输入的密码不一致');
					return false;
				}
				
				return true;
			},
			
			// 提交注册
			doRegist() {
				if (!this.agreeTerms) {
					this.showToast('请先阅读并同意服务协议和隐私政策');
					return;
				}
				
				if (!this.validateForm()) {
					return;
				}
				
				this.loading = true;
				
				// 发起注册请求
				uni.request({
					url: 'http://localhost:8083/wxapi/register',
					method: 'POST',
					data: this.formData,
					success: (res) => {
						console.log("注册结果:", res.data);
						
						if (res.data.code == 200) {
							// 注册成功
							uni.showToast({
								title: '注册成功',
								icon: 'success',
								duration: 2000
							});
							
							// 跳转到登录页
							setTimeout(() => {
								uni.redirectTo({
									url: '/pages/login/login'
								});
							}, 2000);
						} else {
							// 注册失败
							this.showToast(res.data.msg || '注册失败，请稍后重试');
						}
					},
					fail: () => {
						this.showToast('网络异常，请稍后重试');
					},
					complete: () => {
						this.loading = false;
					}
				});
			},
			
			// 显示消息提示
			showToast(message) {
				uni.showToast({
					title: message,
					icon: 'none',
					duration: 2000
				});
			}
		}
	}
</script>

<style>
	/* 重置和基础样式 */
	page {
		margin: 0;
		padding: 0;
		height: 100%;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
	}
	
	/* 主容器 */
	.regist-container {
		width: 100%;
		height: 100vh;
		position: relative;
		display: flex;
		flex-direction: column;
	}
	
	/* 背景图片与渐变 */
	.bg-img {
		position: fixed;
		width: 100%;
		height: 100%;
		top: 0;
		left: 0;
		z-index: -2;
		object-fit: cover;
	}
	
	.gradient-overlay {
		position: fixed;
		width: 100%;
		height: 100%;
		top: 0;
		left: 0;
		z-index: -1;
		background: linear-gradient(to bottom, rgba(0, 65, 155, 0.5), rgba(0, 150, 255, 0.7));
	}
	
	/* 全屏卡片 */
	.regist-card {
		flex: 1;
		display: flex;
		flex-direction: column;
		width: 100%;
		min-height: 100%;
		position: relative;
		background-color: #ffffff;
	}
	
	/* 波浪装饰 */
	.wave-decoration {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 260rpx;
		background: linear-gradient(135deg, #0062cc, #007bff);
		border-bottom-left-radius: 40% 30rpx;
		border-bottom-right-radius: 40% 30rpx;
		z-index: 0;
	}
	
	/* 返回按钮 */
	.back-button {
		position: absolute;
		top: 60rpx;
		left: 30rpx;
		width: 70rpx;
		height: 70rpx;
		border-radius: 50%;
		background-color: rgba(255, 255, 255, 0.15);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 5;
	}
	
	/* 头部区域 */
	.header-section {
		padding: 60rpx 50rpx 0;
		position: relative;
		z-index: 1;
	}
	
	.brand-container {
		display: flex;
		align-items: center;
		margin: 80rpx 0 40rpx;
	}
	
	.brand-logo {
		width: 90rpx;
		height: 90rpx;
		margin-right: 20rpx;
	}
	
	.brand-text {
		display: flex;
		flex-direction: column;
	}
	
	.brand-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #ffffff;
		margin-bottom: 6rpx;
	}
	
	.brand-slogan {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.9);
	}
	
	/* 主体内容区 */
	.regist-content {
		flex: 1;
		padding: 220rpx 50rpx 40rpx;
		background-color: #ffffff;
		border-top-left-radius: 40rpx;
		border-top-right-radius: 40rpx;
		margin-top: -140rpx;
		position: relative;
		z-index: 2;
		box-shadow: 0 -20rpx 30rpx rgba(0, 0, 0, 0.05);
		display: flex;
		flex-direction: column;
	}
	
	.regist-header {
		margin-bottom: 40rpx;
	}
	
	.regist-title {
		font-size: 46rpx;
		font-weight: bold;
		color: #0056b3;
		margin-bottom: 14rpx;
	}
	
	.regist-subtitle {
		font-size: 28rpx;
		color: #6c757d;
	}
	
	/* 表单滚动区域 */
	.form-scroll-area {
		flex: 1;
		width: 100%;
	}
	
	/* 表单样式 */
	.regist-form {
		width: 100%;
		padding-bottom: 40rpx;
	}
	
	.form-item {
		margin-bottom: 24rpx;
		display: flex;
		align-items: center;
		background-color: #f8faff;
		border-radius: 16rpx;
		border: 2rpx solid #e6efff;
		height: 100rpx;
		overflow: hidden;
		transition: all 0.3s;
		position: relative;
		padding-right: 20rpx;
	}
	
	.form-item:focus-within {
		border-color: #007bff;
		box-shadow: 0 0 0 3rpx rgba(0, 123, 255, 0.1);
		background-color: #ffffff;
	}
	
	.input-icon {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 70rpx;
		height: 100%;
		margin: 0 5rpx;
	}
	
	.input-label {
		font-size: 28rpx;
		color: #666;
		width: 120rpx;
		padding-right: 10rpx;
		text-align: left;
	}
	
	.native-input {
		flex: 1;
		height: 100rpx;
		line-height: 100rpx;
		font-size: 28rpx;
		color: #333;
		background-color: transparent;
		border: none;
	}
	
	/* 银行选择器 */
	.bank-picker {
		flex: 1;
		height: 100%;
	}
	
	.picker-content {
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-right: 20rpx;
	}
	
	.picker-text {
		font-size: 28rpx;
		color: #333;
	}
	
	/* 服务协议 */
	.agreement-row {
		display: flex;
		justify-content: center;
		margin: 10rpx 0 30rpx;
	}
	
	.agreement-option {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		justify-content: center;
	}
	
	.custom-checkbox {
		width: 36rpx;
		height: 36rpx;
		border-radius: 8rpx;
		border: 2rpx solid #c0d4f2;
		background-color: #ffffff;
		margin-right: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.custom-checkbox.checked {
		background-color: #007bff;
		border-color: #007bff;
	}
	
	.agreement-text {
		font-size: 26rpx;
		color: #6c757d;
	}
	
	.agreement-link {
		font-size: 26rpx;
		color: #007bff;
		margin: 0 4rpx;
	}
	
	/* 注册按钮 */
	.regist-button {
		width: 100%;
		height: 100rpx;
		background: linear-gradient(to right, #0062cc, #007bff);
		border-radius: 16rpx;
		color: #ffffff;
		font-size: 32rpx;
		font-weight: 600;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 20rpx 0 30rpx;
		position: relative;
		overflow: hidden;
		border: none;
		box-shadow: 0 8rpx 20rpx rgba(0, 123, 255, 0.3);
	}
	
	.regist-button::after {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0));
		transition: all 0.6s;
	}
	
	.regist-button:active::after {
		left: 100%;
	}
	
	.button-disabled {
		opacity: 0.7;
		background: linear-gradient(to right, #6c757d, #adb5bd);
		box-shadow: none;
	}
	
	/* 登录链接 */
	.login-link-row {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-top: 20rpx;
	}
	
	.login-tip {
		font-size: 28rpx;
		color: #6c757d;
	}
	
	.login-link {
		font-size: 28rpx;
		color: #007bff;
		font-weight: bold;
		margin-left: 10rpx;
	}
	
	/* 底部装饰 */
	.footer-decoration {
		display: flex;
		justify-content: center;
		align-items: center;
		gap: 20rpx;
		padding: 20rpx 0;
		background: #f8f9fa;
	}
	
	.decoration-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background-color: #007bff;
		opacity: 0.6;
	}
	
	.decoration-dot:nth-child(2) {
		width: 8rpx;
		height: 8rpx;
	}
	
	/* 适配小屏设备 */
	@media screen and (max-width: 375px) {
		.regist-content {
			padding: 200rpx 40rpx 30rpx;
		}
		
		.regist-title {
			font-size: 40rpx;
		}
		
		.form-item {
			height: 90rpx;
		}
		
		.regist-button {
			height: 90rpx;
			font-size: 30rpx;
		}
	}
</style>
