<template>
    <view class="login-container">
        <!-- 背景图片与渐变遮罩 -->
        <!-- <image class="bg-img" src="../../static/bannerlist2.jpg" mode="aspectFill"></image> -->
        <view class="gradient-overlay"></view>
        
        <!-- 全屏登录卡片 -->
        <view class="login-card">
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
            <view class="login-content">
                <view class="login-header">
                    <text class="login-title">登录</text>
                </view>

                <uni-forms ref="form" :model="formData" :rules="rules" class="login-form">
                    <!-- 身份证号输入 -->
                    <view class="form-item">
                        <view class="input-icon">
                            <uni-icons type="person-filled" size="24" color="#0056b3"></uni-icons>
                        </view>
                        <view class="input-label">身份证号</view>
                        <input 
                            v-model="formData.idNumber" 
                            type="idcard"
                            placeholder="请输入身份证号" 
                            class="native-input"
                            maxlength="18"
                        />
                    </view>
                    
                    <!-- 密码输入 -->
                    <view class="form-item">
                        <view class="input-icon">
                            <uni-icons type="locked-filled" size="24" color="#0056b3"></uni-icons>
                        </view>
                        <view class="input-label">密码</view>
                        <input 
                            v-model="formData.passwd" 
                            type="password" 
                            placeholder="请输入密码"
                            class="native-input"
                            password
                        />
                    </view>
                    
                    <!-- 验证码输入 -->
                    <view class="form-item captcha-item">
                        <view class="input-icon">
                            <uni-icons type="shield-filled" size="24" color="#0056b3"></uni-icons>
                        </view>
                        <view class="input-label">验证码</view>
                        <input 
                            v-model="formData.Verification" 
                            type="text" 
                            placeholder="请输入验证码"
                            class="native-input captcha-input"
                        />
                        
                        <!-- 验证码图片 -->
                        <view class="captcha-box" @tap="doFlush">
                            <image 
                                :src="picture+'?id='+varId" 
                                class="captcha-image" 
                                mode="aspectFit" 
                                :style="loadingCaptcha ? 'opacity: 0.6;' : ''"
                            ></image>
                        </view>
                    </view>
                    
                    <!-- 记住密码选项 -->
                    <view class="option-row">
                        <view class="remember-option" @tap="rememberPassword = !rememberPassword">
                            <view class="custom-checkbox" :class="{'checked': rememberPassword}">
                                <uni-icons v-if="rememberPassword" type="checkmarkempty" size="14" color="#ffffff"></uni-icons>
                            </view>
                            <text class="option-text">记住身份证号</text>
                        </view>
                        <text class="forget-link" @tap="goToRegister">注册账号?</text>
                    </view>
                    
                    <!-- 登录按钮 -->
                    <button 
                        class="login-button" 
                        :loading="loading" 
                        :disabled="loading"
                        @tap="doLogin"
                    >
                        {{ loading ? '登录中...' : '登录' }}
                    </button>
                    
                    <!-- 其他登录方式 -->
                   <!-- <view class="login-methods">
                        <view class="method-divider">
                            <view class="divider-line"></view>
                            <text class="divider-text">其他登录方式</text>
                            <view class="divider-line"></view>
                        </view>
                        
                        <view class="method-icons">
                            <view class="method-icon wechat" @tap="loginWithMethod('wechat')">
                                <uni-icons type="weixin" size="26" color="#ffffff"></uni-icons>
                            </view>
                            <view class="method-icon mobile" @tap="loginWithMethod('mobile')">
                                <uni-icons type="phone-filled" size="26" color="#ffffff"></uni-icons>
                            </view>
                            <view class="method-icon register" @tap="doRegister">
                                <uni-icons type="personadd-filled" size="26" color="#ffffff"></uni-icons>
                            </view>
                        </view>
                    </view> -->
                </uni-forms>
            </view>
            
            <!-- 底部版权信息 -->
            <view class="footer-section">
                <text class="footer-text">登录即表示您同意《服务协议》和《隐私政策》</text>
                <text class="copyright">© 2023 智能管理系统 版权所有</text>
            </view>
        </view>

        <!-- 提示框 -->
        <uni-popup ref="popup" type="dialog">
            <uni-popup-dialog 
                mode="base" 
                title="提示" 
                :content="msg" 
                :duration="2000" 
                :before-close="true" 
                @close="close"
                @confirm="confirm"
            ></uni-popup-dialog>
        </uni-popup>
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
                varId: 1,
                picture: "http://localhost:8083/wxapi/getVerify",
                msg: null,
                loadingCaptcha: false,
                loading: false,
                rememberPassword: false,
                inputStyles: {
                    borderColor: 'transparent'
                },
                formData: {
                    idNumber: null,
                    passwd: null,
                    Verification: null,
                },
                rules: {
                    idNumber: {
                        rules: [{
                            required: true,
                            errorMessage: '请输入身份证号',
                        }, {
                            pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
                            errorMessage: '请输入正确的身份证号',
                        }]
                    },
                    passwd: {
                        rules: [{
                            required: true,
                            errorMessage: '请输入密码',
                        }, {
                            minLength: 6,
                            errorMessage: '密码长度不能少于6位',
                        }]
                    },
                    Verification: {
                        rules: [{
                            required: true,
                            errorMessage: '请输入验证码',
                        }]
                    },
                }
            }
        },
        onLoad() {
            // 页面加载时刷新验证码
            this.doFlush();
            // 检查是否有存储的身份证号
            const savedIdNumber = uni.getStorageSync('saved_id_number');
			console.log("验证码验证结果: ",savedIdNumber);
            if (savedIdNumber) {
                this.formData.idNumber = savedIdNumber;
                this.rememberPassword = true;
			}
		},
		methods: {
            // 刷新验证码
            doFlush() {
                this.loadingCaptcha = true;
                // 增加id确保每次请求不会被缓存
                this.varId = Date.now();
                
                // 模拟加载状态，刷新验证码需要时间
                setTimeout(() => {
                    this.loadingCaptcha = false;
                }, 300);
            },
            
            // 注册页面跳转
            doRegister() {
                uni.navigateTo({
                    url: '/pages/login/add_user'
                })
            },
            
            // 返回按钮处理
            goBack() {
				console.log(11111)
                uni.navigateTo({
                    url: '/pages/login/TotalLogin'
                })
            },
            
            // 使用其他方式登录
            loginWithMethod(type) {
                this.msg = `暂未开通${type === 'wechat' ? '微信' : '手机号'}登录方式`;
                this.showToast(this.msg);
            },
            
            // 登录处理
            doLogin() {
                this.loading = true;
                
                // 手动验证表单数据
                let hasError = false;
                let errorMsg = "";
                
                // 验证身份证号
                if (!this.formData.idNumber) {
                    hasError = true;
                    errorMsg = "请输入身份证号";
                } else {
                    const idPattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                    if (!idPattern.test(this.formData.idNumber)) {
                        hasError = true;
                        errorMsg = "请输入正确的身份证号";
                    }
                }
                
                // 验证密码
                if (!hasError && !this.formData.passwd) {
                    hasError = true;
                    errorMsg = "请输入密码";
                } else if (!hasError && this.formData.passwd.length < 6) {
                    hasError = true;
                    errorMsg = "密码长度不能少于6位";
                }
                
                // 验证验证码
                if (!hasError && !this.formData.Verification) {
                    hasError = true;
                    errorMsg = "请输入验证码";
                }
                
                if (hasError) {
                    this.showToast(errorMsg);
                    this.loading = false;
                    return;
                }
                
                // 验证通过，验证验证码
                uni.request({
                    url: 'http://localhost:8083/wxapi/checkVerify?vaibleStr=' + this.formData.Verification,
                    method: 'GET',
                    success: (res) => {
                        console.log("验证码验证结果: ", res.data);
                        
                        if (res.data == true) {
                            // 验证码正确，继续登录流程
                            this.submitLogin();
                        } else {
                            // 验证码错误
                            this.showToast("验证码错误，请重新输入");
                            this.doFlush(); // 刷新验证码
                            this.formData.Verification = null; // 清空验证码输入
                            this.loading = false;
                        }
                    },
                    fail: (err) => {
                        this.showToast("网络异常，请稍后重试");
                        this.loading = false;
                    }
                });
            },
            
            // 提交登录请求
            submitLogin() {
                uni.request({
                    url: "http://localhost:8083/wxapi/login",
                    method: 'POST',
                    data: {
                        idCard: this.formData.idNumber,
                        loginPassword: this.formData.passwd
                    },
                    success: (res) => {
                        console.log("登录结果: ", res.data);	
                        
                        if (res.data.code == 200) {
                            // 登录成功
                            const userData = res.data.data;
                            console.log("完整用户数据:", userData);
                            
                            // 提取角色信息
                            const userRole = userData[0].userrole || userData[0].role || '';
                            console.log("用户角色:", userRole[0], "类型:", typeof userRole);
                            
                            // 确保数据格式与 me.vue 页面一致
                            const userInfo = {
                                id: userData.id || userData.idCard || this.formData.idNumber,
                                idCard: userData.idCard || this.formData.idNumber,
                                avatar: userData.avatar || 'https://portrait.gitee.com/uploads/avatars/user/354/1062657_rahman_1649233036.png!avatar60',
                                nickname: userData.nickname || userData.realName || userData.name || '用户' + this.formData.idNumber.substring(this.formData.idNumber.length - 4),
                                // 明确保存角色信息
                                role: userRole,
                                // 其他字段
                                phone: userData.phone || '',
                                email: userData.email || '',
                            };
                            
                            // 存储到本地
                            uni.setStorageSync('user_info', userData);
                            setUserInfo(userInfo);
                            setToken(userData.token || userData.id || this.formData.idNumber);
                            
                            // 记住身份证号
                            if (this.rememberPassword) {
                                uni.setStorageSync('saved_id_number', this.formData.idNumber);
                            } else {
                                uni.removeStorageSync('saved_id_number');
                            }
                            
                            // 显示成功提示
                            uni.showToast({
                                title: '登录成功',
                                icon: 'success',
                                duration: 1500
                            });
                            
                            // 根据用户角色跳转到不同页面
                            setTimeout(() => {
                                // 检查角色，使用更健壮的方式
                                const roleStr = String(userRole).toLowerCase();
                                const isAdmin = roleStr.includes('admin') || roleStr === 'administrator';
                                const isReviewer = roleStr.includes('review') || roleStr === 'evaluator' || roleStr === 'reviewer';
                                
                                if (isAdmin) {
                                    console.log("角色匹配成功：", roleStr, "跳转到管理页面");
                                    // 管理员或评议员跳转到管理页面
                                    uni.reLaunch({
                                        url: "/pages/me/me"
                                    });
                                } else if(isReviewer) {
									uni.reLaunch({
									    url: "/pages/admin/shenpiList"
									});
								}
								else {
                                    console.log("普通用户角色：", roleStr, "跳转到首页");
                                    // 普通用户跳转到首页
                                    uni.reLaunch({
                                        url: "/pages/index/index"
                                    });
                                }
                            }, 1500);
                        } else {
                            // 登录失败
                            this.showToast(res.data.msg || "登录失败，请检查身份证号和密码");
                            this.doFlush(); // 刷新验证码
                        }
                    },
                    fail: (err) => {
                        this.showToast("网络异常，请稍后重试");
                    },
                    complete: () => {
                        this.loading = false;
                    }
                });
            },

            // 使用uni.showToast显示消息，替代popup
            showToast(message) {
                this.msg = message;
                uni.showToast({
                    title: message,
                    icon: 'none',
                    duration: 2000
                });
            },
            
            // 关闭提示框
            close() {
                this.$refs.popup.close();
            },
            
            // 确认提示框
            confirm(value) {
                this.$refs.popup.close();
            },
            
            // 跳转到注册页面
            goToRegister() {
                uni.navigateTo({
                    url: '/pages/regist/regist'
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
    .login-container {
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
    .login-card {
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
    .login-content {
        flex: 1;
        padding: 220rpx 50rpx 40rpx;
        background-color: #ffffff;
        border-top-left-radius: 40rpx;
        border-top-right-radius: 40rpx;
        margin-top: -140rpx;
        position: relative;
        z-index: 2;
        box-shadow: 0 -20rpx 30rpx rgba(0, 0, 0, 0.05);
    }
    
    .login-header {
        margin-bottom: 60rpx;
		text-align: center;
    }
    
    .login-title {
        font-size: 46rpx;
        font-weight: bold;
        color: #0056b3;
        margin-bottom: 14rpx;
    }
    
    .login-subtitle {
        font-size: 28rpx;
        color: #6c757d;
    }
    
    /* 表单样式 */
    .login-form {
        width: 100%;
    }
    
    .form-item {
        margin-bottom: 28rpx;
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
        width: 140rpx;
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
    
    /* 验证码特殊样式 */
    .captcha-item {
        padding-right: 0;
    }
    
    .captcha-box {
        height: 100%;
        width: 200rpx;
        position: relative;
        overflow: hidden;
        display: flex;
        align-items: center;
        justify-content: center;
        border-left: 1px solid #e6efff;
        background-color: #f0f7ff;
    }
    
    .captcha-image {
        width: 100%;
        height: 80%;
        object-fit: contain;
    }
    
    .captcha-refresh-icon {
        position: absolute;
        bottom: 10rpx;
        right: 10rpx;
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        background-color: rgba(0, 123, 255, 0.8);
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    /* 选项行 */
    .option-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 40rpx;
        padding: 0 10rpx;
    }
    
    .remember-option {
        display: flex;
        align-items: center;
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
    
    .option-text {
        font-size: 26rpx;
        color: #6c757d;
    }
    
    .forget-link {
        font-size: 26rpx;
        color: #007bff;
    }
    
    /* 登录按钮 */
    .login-button {
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
        margin: 20rpx 0 60rpx;
        position: relative;
        overflow: hidden;
        border: none;
        box-shadow: 0 8rpx 20rpx rgba(0, 123, 255, 0.3);
    }
    
    .login-button::after {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0));
        transition: all 0.6s;
    }
    
    .login-button:active::after {
        left: 100%;
    }
    
    /* 其他登录方式 */
    .login-methods {
        padding: 20rpx 0;
    }
    
    .method-divider {
        display: flex;
        align-items: center;
        margin-bottom: 40rpx;
    }
    
    .divider-line {
        flex: 1;
        height: 1px;
        background-color: #e6efff;
    }
    
    .divider-text {
        padding: 0 30rpx;
        font-size: 24rpx;
        color: #8a93a2;
    }
    
    .method-icons {
        display: flex;
        justify-content: center;
        gap: 60rpx;
    }
    
    .method-icon {
        width: 100rpx;
        height: 100rpx;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
        transition: transform 0.3s;
    }
    
    .method-icon:active {
        transform: scale(0.95);
    }
    
    .wechat {
        background: linear-gradient(135deg, #52c234, #09bb07);
    }
    
    .mobile {
        background: linear-gradient(135deg, #ff512f, #dd2476);
    }
    
    .register {
        background: linear-gradient(135deg, #396afc, #2948ff);
    }
    
    /* 底部版权 */
    .footer-section {
        padding: 30rpx 50rpx;
        text-align: center;
        background-color: #f8faff;
    }
    
    .footer-text {
        font-size: 24rpx;
        color: #8a93a2;
        margin-bottom: 10rpx;
        display: block;
    }
    
    .copyright {
        font-size: 22rpx;
        color: #a8aeba;
        display: block;
    }
    
    /* 适配小屏设备 */
    @media screen and (max-width: 375px) {
        .login-content {
            padding: 200rpx 40rpx 30rpx;
        }
        
        .login-title {
            font-size: 40rpx;
        }
        
        .form-item {
            height: 90rpx;
        }
        
        .login-button {
            height: 90rpx;
            font-size: 30rpx;
        }
        
        .method-icon {
            width: 90rpx;
            height: 90rpx;
        }
    }
</style>
