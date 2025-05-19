<template>
	<view class="user-container">
		<!-- 顶部标题与搜索区 -->
		<view class="header">
			<view class="title-section">
				<text class="page-title">用户管理</text>
				<text class="subtitle">管理系统用户信息</text>
			</view>
			
			<view class="action-area">
				<view class="search-box">
					<input type="text" placeholder="搜索用户" v-model="searchKeyword" @input="handleSearch" />
					<text class="search-icon">搜</text>
				</view>
				<view class="add-btn" @click="showAddUserModal">
					<text>添加用户</text>
				</view>
			</view>
			
			<!-- 添加刷新按钮 -->
			<!-- <view class="action-area" style="margin-top: 20rpx;">
				<view class="refresh-btn" @click="loadUserList">
					<text>刷新数据</text>
				</view>
				<view class="refresh-btn" @click="retryLoadUsers" style="background: linear-gradient(135deg, #FF7A45, #FAAD14);">
					<text>修复连接</text>
				</view>
			</view> -->
		</view>

		<!-- 筛选区域 -->
		<view class="filter-section">
			<view class="filter-item" 
				v-for="(item, index) in filterOptions" 
				:key="index"
				:class="{'active': activeFilter === item.value}"
				@click="setFilter(item.value)">
				<text>{{ item.label }}</text>
			</view>
		</view>
		
		<!-- 用户列表 -->
		<view class="user-list">
			<view class="empty-tip" v-if="filteredUsers.length === 0">
				<text class="empty-icon">i</text>
				<text>暂无用户数据</text>
			</view>
			
			<view class="user-item" v-for="(user, index) in filteredUsers" :key="user.id || index">
				<!-- 添加调试信息 -->
				<view v-if="index === 0" style="display: none;">{{ JSON.stringify(user) }}</view>
				
				<view class="user-avatar">
					<text>{{ user.name ? user.name.substring(0, 1) : 'U' }}</text>
				</view>
				
				<view class="user-info">
					<view class="user-main-info">
						<text class="user-name">{{ user.name || '未知用户' }}</text>
						<text class="user-type" :class="'type-' + (user.type === 'user' ? 'applicant' : user.type || 'applicant')">
							{{ getUserTypeName(user.type) }}
						</text>
					</view>
					<view class="user-details">
						<text class="user-phone">{{ user.phone || '无电话' }}</text>
						<text class="user-date">注册时间: {{ user.registrationDate || '未知' }}</text>
					</view>
				</view>
				
				<view class="user-actions">
					<view class="action-btn edit-btn" @tap.stop="editUserSimple(index)">
						<text>编辑</text>
					</view>
					<view class="action-btn delete-btn" @tap.stop="confirmDeleteSimple(index)">
						<text>删除</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 用户表单弹窗 -->
		<view class="modal-overlay" v-if="showModal" @click="closeModal"></view>
		<view class="user-modal" v-if="showModal">
			<view class="modal-header">
				<text class="modal-title">{{ isEditing ? '编辑用户' : '添加用户' }}</text>
				<text class="modal-close" @click="closeModal">×</text>
			</view>
			
			<view class="modal-body">
				<view class="form-group">
					<text class="form-label">姓名</text>
					<input type="text" v-model="formData.name" placeholder="请输入姓名" />
				</view>
				
				<view class="form-group">
					<text class="form-label">手机号</text>
					<input type="text" v-model="formData.phone" placeholder="请输入手机号" />
				</view>
				
				<view class="form-group">
					<text class="form-label">身份证号</text>
					<input type="text" v-model="formData.idCard" placeholder="请输入身份证号" />
				</view>
				
				<view class="form-group">
					<text class="form-label">用户类型</text>
					<view class="radio-group">
						<view class="radio-item" 
							v-for="(type, index) in userTypes" 
							:key="index"
							:class="{'active': formData.type === type.value}"
							@click="formData.type = type.value">
							<text>{{ type.label }}</text>
						</view>
					</view>
				</view>
				
				<view class="form-group">
					<text class="form-label">账号状态</text>
					<view class="radio-group">
						<view class="radio-item" 
							v-for="(status, index) in statusOptions" 
							:key="index"
							:class="{'active': formData.status === status.value}"
							@click="formData.status = status.value">
							<text>{{ status.label }}</text>
						</view>
					</view>
				</view>
				
				<view class="form-group">
					<text class="form-label">地址</text>
					<input type="text" v-model="formData.address" placeholder="请输入地址" />
				</view>
				
				<view class="form-group">
					<text class="form-label">银行名称</text>
					<input type="text" v-model="formData.bankName" placeholder="请输入银行名称" />
				</view>
				
				<view class="form-group">
					<text class="form-label">银行账号</text>
					<input type="text" v-model="formData.bankAccount" placeholder="请输入银行账号" />
				</view>
				
				<view class="form-group" v-if="!isEditing">
					<text class="form-label">初始密码</text>
					<input type="text" v-model="formData.password" placeholder="请输入初始密码" />
				</view>
				
				<view class="form-group remarks">
					<text class="form-label">备注</text>
					<textarea v-model="formData.remarks" placeholder="请输入备注信息"></textarea>
				</view>
			</view>
			
			<view class="modal-footer">
				<view class="modal-btn cancel-btn" @click="closeModal">
					<text>取消</text>
				</view>
				<view class="modal-btn confirm-btn" @click="saveUser">
					<text>保存</text>
				</view>
			</view>
		</view>
		
		<!-- 删除确认弹窗 -->
		<view class="modal-overlay" v-if="showDeleteConfirm" @click="cancelDelete"></view>
		<view class="delete-confirm-modal" v-if="showDeleteConfirm">
			<view class="modal-header">
				<text class="modal-title">删除确认</text>
			</view>
			
			<view class="modal-body">
				<text class="confirm-text">确定要删除用户 "{{ userToDelete.name }}" 吗？</text>
				<text class="confirm-warning">此操作不可恢复，请谨慎操作。</text>
			</view>
			
			<view class="modal-footer">
				<view class="modal-btn cancel-btn" @click="cancelDelete">
					<text>取消</text>
				</view>
				<view class="modal-btn delete-btn" @click="deleteUser">
					<text>确认删除</text>
				</view>
			</view>
		</view>
		
		<!-- 测试按钮 -->
		<!-- <view class="add-btn" style="margin-top: 20rpx;" @click="testModal">
			<text>测试弹窗</text>
		</view> -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 用户列表
				users: [],
				// 搜索关键词
				searchKeyword: '',
				// 筛选选项
				filterOptions: [
					{ label: '全部用户', value: 'all' },
					{ label: '申请人', value: 'user' },
					{ label: '管理员', value: 'admin' },
					{ label: '已禁用', value: 'inactive' }
				],
				activeFilter: 'all',
				// 用户类型选项
				userTypes: [
					{ label: '申请人', value: 'user' },
					{ label: '管理员', value: 'admin' }
				],
				// 状态选项
				statusOptions: [
					{ label: '正常', value: 'active' },
					{ label: '禁用', value: 'inactive' }
				],
				// 表单数据
				formData: {
					id: null,
					name: '',
					phone: '',
					idCard: '',
					type: 'applicant',
					status: 'active',
					address: '',
					password: '',
					remarks: '',
					bankName: '',
					bankAccount: ''
				},
				// 控制弹窗显示
				showModal: false,
				isEditing: false,
				showDeleteConfirm: false,
				userToDelete: {}
			}
		},
		computed: {
			// 过滤后的用户列表
			filteredUsers() {
				return this.users.filter(user => {
					// 添加安全检查
					const name = user.name || '';
					const phone = user.phone || '';
					const idCard = user.idCard || '';
					const type = user.type || '';
					
					// 搜索过滤
					const searchMatch = this.searchKeyword === '' || 
						name.includes(this.searchKeyword) || 
						phone.includes(this.searchKeyword) ||
						idCard.includes(this.searchKeyword);
					
					// 类型过滤
					let typeMatch = true;
					if (this.activeFilter !== 'all') {
						if (this.activeFilter === 'inactive') {
							typeMatch = user.status === 'inactive';
						} else {
							typeMatch = type === this.activeFilter;
						}
					}
					
					// 调试信息
					console.log(`用户 ${name} 类型: ${type}, 筛选条件: ${this.activeFilter}, 匹配结果: ${typeMatch}`);
					
					return searchMatch && typeMatch;
				});
			}
		},
		mounted() {
			// 页面加载时获取用户数据
			this.loadUserList();
		},
		methods: {
			// 加载用户列表 - 增加更详细的调试信息
			loadUserList() {
				// 显示加载状态
				uni.showLoading({
					title: '加载中...'
				});
				
				// 尝试从本地存储获取baseUrl
				const baseUrl = uni.getStorageSync('baseUrl') || '';
				console.log('当前baseUrl:', baseUrl);
				
				// 检查baseUrl是否存在
				if (!baseUrl) {
					console.error('baseUrl未设置!');
					// uni.showToast({
					// 	title: 'API地址未配置',
					// 	icon: 'none'
					// });
					// 创建一些模拟数据
					this.users = [this.createDefaultUser()];
					uni.hideLoading();
					return;
				}
				
				// 构建请求URL和参数
				const url = `${baseUrl}/wxapi/user/query`;
				
				// 只有是特定用户类型过滤时才添加参数
				const params = {};
				if (this.activeFilter !== 'all' && this.activeFilter !== 'inactive') {
					params.userrole = this.activeFilter;
				}
				
				console.log('请求URL:', url);
				console.log('请求参数:', params);
				
				// 发送请求
				uni.request({
					url: url,
					method: 'GET',
					data: params,
					success: (res) => {
						console.log('API返回完整响应:', res);
						console.log('API返回数据:', res.data);
						
						// 检查响应状态码
						if (res.statusCode !== 200) {
							console.error('HTTP状态码错误:', res.statusCode);
							this.users = [this.createDefaultUser()];
							uni.showToast({
								title: `请求错误: ${res.statusCode}`,
								icon: 'none'
							});
							return;
						}
						
						if (res.data.code === 200 && Array.isArray(res.data.data)) {
							console.log('成功获取用户数据, 条数:', res.data.data.length);
							
							// 转换后端数据格式为前端使用格式
							this.users = res.data.data.map(user => {
								// 检查必要字段是否存在
								if (!user) return this.createDefaultUser();
								
								// 打印当前处理的用户数据，帮助调试
								console.log('处理用户数据:', user);
								
								// 确保用户类型字段被正确映射
								const userType = user.userrole || 'user'; // 默认为普通用户
								console.log(`用户 ${user.realName} 的类型: ${userType}`);
								
								return {
									id: user.id || Date.now() + Math.floor(Math.random() * 1000),
									name: user.realName || '未命名用户',
									phone: user.contact || '未设置',
									idCard: user.idCard || '未设置',
									type: userType, // 确保类型正确赋值
									status: 'active', // 后端数据中没有明确标识状态的字段，默认为active
									address: user.domicile || '',
									registrationDate: this.formatTimestamp(user.createTime) || '未知',
									remarks: user.remark || '',
									bankName: user.bankName || '',
									bankAccount: user.bankAccount || ''
								};
							});
							
							// 如果是按禁用状态筛选，在前端处理
							if (this.activeFilter === 'inactive') {
								this.users = this.users.filter(user => user.status === 'inactive');
							}
						} else {
							// 请求成功但数据格式不正确
							console.error('API返回数据格式不正确:', res.data);
							
							// 尝试检查常见的响应格式，可能服务端更改了响应结构
							if (res.data && typeof res.data === 'object') {
								if (res.data.msg) {
									console.log('API返回消息:', res.data.msg);
								}
								
								// 检查是否有不同名称的数据字段
								const possibleDataFields = ['rows', 'list', 'items', 'result', 'results'];
								for (const field of possibleDataFields) {
									if (Array.isArray(res.data[field])) {
										console.log(`发现可能的数据字段: ${field}, 长度: ${res.data[field].length}`);
									}
								}
							}
							
							this.users = [this.createDefaultUser()];
							
							uni.showToast({
								title: '获取用户数据格式不正确',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败详情:', err);
						// 请求失败时使用默认数据
						this.users = [this.createDefaultUser()];
						
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
				
				// 添加超时处理
				setTimeout(() => {
					if (uni.hideLoading) {
						uni.hideLoading();
					}
				}, 10000); // 10秒超时
			},
			
			// 添加一个手动重试方法
			retryLoadUsers() {
				// 刷新baseUrl
				const baseUrl = prompt('请输入API基础地址:', uni.getStorageSync('baseUrl') || '');
				if (baseUrl) {
					uni.setStorageSync('baseUrl', baseUrl);
					this.loadUserList();
				}
			},
			
			// 创建默认用户对象 - 根据新的字段结构更新
			createDefaultUser() {
				return {
					id: Date.now(),
					name: '测试用户',
					phone: '13800000000',
					idCard: '110101199001010000',
					type: 'user',
					status: 'active',
					address: '未设置地址',
					registrationDate: this.formatDate(new Date()),
					remarks: '默认用户数据',
					bankName: '',
					bankAccount: ''
				};
			},
			
			// 处理搜索
			handleSearch() {
				// 搜索用户
				this.loadUserList();
			},
			
			// 设置过滤条件
			setFilter(filterValue) {
				console.log('设置过滤条件:', filterValue);
				this.activeFilter = filterValue;
				this.loadUserList();
			},
			
			// 获取用户类型名称 - 更新用户角色映射
			getUserTypeName(type) {
				// 确保类型是有效的字符串
				const userType = type || '';
				console.log('获取用户类型名称:', userType);
				
				const typeMap = {
					'user': '申请人',
					'reviewer': '评议员',
					'admin': '管理员',
					'applicant': '申请人' // 兼容旧值
				};
				
				// 检查类型是否存在于映射中
				if (typeMap[userType]) {
					return typeMap[userType];
				}
				
				// 如果找不到匹配的类型，记录日志并返回默认值
				console.warn('未知的用户类型:', userType);
				return '普通用户'; // 更友好的默认显示
			},
			
			// 显示添加用户弹窗
			showAddUserModal() {
				this.isEditing = false;
				this.formData = {
					id: null,
					name: '',
					phone: '',
					idCard: '',
					type: 'applicant',
					status: 'active',
					address: '',
					password: '123456', // 默认密码
					remarks: '',
					bankName: '',
					bankAccount: ''
				};
				this.showModal = true;
			},
			
			// 添加一个简化版的编辑用户方法，使用索引而不是对象引用
			editUserSimple(index) {
				console.log('编辑用户索引:', index);
				const user = this.filteredUsers[index];
				
				if (!user) {
					console.error('用户不存在，索引:', index);
					uni.showToast({
						title: '用户数据不存在',
						icon: 'none'
					});
					return;
				}
				
				console.log('找到用户数据:', user);
				
				// 设置编辑状态和表单数据
				this.isEditing = true;
				this.formData = {
					id: user.id,
					name: user.name || '',
					phone: user.phone || '',
					idCard: user.idCard || '',
					type: user.type || 'user',
					status: user.status || 'active',
					address: user.address || '',
					remarks: user.remarks || '',
					bankName: user.bankName || '',
					bankAccount: user.bankAccount || ''
				};
				
				// 显示弹窗
				this.showModal = true;
			},
			
			// 关闭弹窗
			closeModal() {
				console.log('关闭弹窗');
				this.showModal = false;
			},
			
			// 保存用户
			saveUser() {
				console.log('尝试保存用户:', this.formData);
				
				if (!this.formData.name || !this.formData.phone || !this.formData.idCard) {
					uni.showToast({
						title: '请填写必要信息',
						icon: 'none'
					});
					return;
				}
				
				// 验证手机号
				if (!/^1\d{10}$/.test(this.formData.phone)) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					});
					return;
				}
				
				// 验证身份证号
				if (!/^\d{17}[\dXx]$/.test(this.formData.idCard)) {
					uni.showToast({
						title: '请输入正确的身份证号',
						icon: 'none'
					});
					return;
				}
				
				// 准备提交到后端的数据 - 适配新的字段结构
				const userData = {
					realName: this.formData.name,
					contact: this.formData.phone,
					idCard: this.formData.idCard,
					userrole: this.formData.type,
					domicile: this.formData.address,
					remark: this.formData.remarks,
					bankName: this.formData.bankName,
					bankAccount: this.formData.bankAccount
				};
				
				if (this.isEditing) {
					userData.id = this.formData.id;
					console.log('正在编辑用户ID:', userData.id);
				}
				
				if (!this.isEditing) {
					userData.loginPassword = this.formData.password || '123456';
				}
				
				// 发送请求
				uni.showLoading({
					title: '保存中...'
				});
				
				const baseUrl = uni.getStorageSync('baseUrl') || '';
				const url = this.isEditing ? 
					`${baseUrl}/wxapi/user/edit` : 
					`${baseUrl}/wxapi/user/add`;
				
				console.log('请求URL:', url);
				console.log('请求数据:', userData);
				
				// 使用正确的请求方法
				const method = this.isEditing ? 'PUT' : 'POST';
				
				uni.request({
					url: url,
					method: method,
					data: userData,
					header: {
						'content-type': 'application/json'
					},
					success: (res) => {
						console.log('保存响应:', res);
						
						if (res.data.code === 200) {
							uni.showToast({
								title: this.isEditing ? '修改成功' : '添加成功',
								icon: 'success'
							});
							this.closeModal();
							// 重新加载用户列表
							this.loadUserList();
						} else {
							uni.showToast({
								title: res.data.msg || '操作失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败：', err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},
			
			// 添加一个简化版的确认删除方法，使用索引
			confirmDeleteSimple(index) {
				console.log('确认删除用户索引:', index);
				const user = this.filteredUsers[index];
				
				if (!user) {
					console.error('用户不存在，索引:', index);
					uni.showToast({
						title: '用户数据不存在',
						icon: 'none'
					});
					return;
				}
				
				console.log('找到要删除的用户:', user);
				this.userToDelete = user;
				this.showDeleteConfirm = true;
			},
			
			// 取消删除
			cancelDelete() {
				this.showDeleteConfirm = false;
				this.userToDelete = {};
			},
			
			// 删除用户 - 增加安全检查
			deleteUser() {
				console.log('开始删除用户:', this.userToDelete);
				
				if (!this.userToDelete || !this.userToDelete.id) {
					uni.showToast({
						title: '删除失败：用户数据无效',
						icon: 'none'
					});
					return;
				}
				
				uni.showLoading({
					title: '删除中...'
				});
				
				const baseUrl = uni.getStorageSync('baseUrl') || '';
				const url = `${baseUrl}/wxapi/user/delete/${this.userToDelete.id}`;
				
				console.log('删除请求URL:', url);
				
				uni.request({
					url: url,
					method: 'DELETE',
					success: (res) => {
						console.log('删除响应:', res);
						
						if (res.data.code === 200) {
							uni.showToast({
								title: '删除成功',
								icon: 'success'
							});
							this.cancelDelete();
							// 重新加载用户列表
							this.loadUserList();
						} else {
							uni.showToast({
								title: res.data.msg || '删除失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败：', err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},
			
			// 格式化时间戳
			formatTimestamp(timestamp) {
				if (!timestamp) return '';
				
				const date = new Date(timestamp);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				
				return `${year}-${month}-${day}`;
			},
			
			// 格式化日期
			formatDate(date) {
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				
				return `${year}-${month}-${day}`;
			},
			
			// 测试弹窗显示
			testModal() {
				this.showModal = true;
				console.log('测试弹窗显示状态:', this.showModal);
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F5F7FA;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	.user-container {
		padding: 30rpx;
	}

	/* 顶部区域样式 */
	.header {
		display: flex;
		flex-direction: column;
		margin-bottom: 30rpx;
	}

	.title-section {
		margin-bottom: 20rpx;
	}

	.page-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 8rpx;
		display: block;
	}

	.subtitle {
		font-size: 24rpx;
		color: #999999;
	}

	.action-area {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.search-box {
		flex: 1;
		height: 70rpx;
		background-color: #FFFFFF;
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		margin-right: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.search-box input {
		flex: 1;
		height: 100%;
		font-size: 26rpx;
	}

	.search-icon {
		font-size: 24rpx;
		color: #999999;
		margin-left: 10rpx;
	}

	.add-btn {
		height: 70rpx;
		background: linear-gradient(135deg, #1890FF, #52C41A);
		border-radius: 35rpx;
		color: #FFFFFF;
		font-size: 26rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.2);
	}

	/* 筛选区域 */
	.filter-section {
		display: flex;
		overflow-x: auto;
		white-space: nowrap;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.filter-item {
		display: inline-block;
		padding: 12rpx 24rpx;
		border-radius: 24rpx;
		font-size: 24rpx;
		color: #666666;
		margin-right: 16rpx;
		transition: all 0.2s ease;
	}

	.filter-item.active {
		background-color: #1890FF;
		color: #FFFFFF;
	}

	/* 用户列表 */
	.user-list {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.user-item {
		display: flex;
		align-items: center;
		padding: 24rpx;
		border-bottom: 1rpx solid #F0F0F0;
	}

	.user-item:last-child {
		border-bottom: none;
	}

	.user-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #1890FF, #52C41A);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
		font-size: 32rpx;
		font-weight: bold;
		color: #FFFFFF;
	}

	.user-info {
		flex: 1;
		margin-right: 20rpx;
	}

	.user-main-info {
		display: flex;
		align-items: center;
		margin-bottom: 8rpx;
	}

	.user-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333333;
		margin-right: 16rpx;
	}

	.user-type {
		font-size: 20rpx;
		padding: 2rpx 12rpx;
		border-radius: 10rpx;
	}

	.type-applicant, .type-user {
		color: #52C41A;
		background-color: rgba(82, 196, 26, 0.1);
	}

	.type-reviewer {
		color: #FAAD14;
		background-color: rgba(250, 173, 20, 0.1);
	}

	.type-admin {
		color: #1890FF;
		background-color: rgba(24, 144, 255, 0.1);
	}

	.user-details {
		display: flex;
		justify-content: space-between;
	}

	.user-phone {
		font-size: 24rpx;
		color: #666666;
	}

	.user-date {
		font-size: 22rpx;
		color: #999999;
	}

	.user-actions {
		display: flex;
	}

	.action-btn {
		width: 80rpx;
		height: 50rpx;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 22rpx;
		margin-left: 10rpx;
	}

	.edit-btn {
		background-color: rgba(24, 144, 255, 0.1);
		color: #1890FF;
	}

	.delete-btn {
		background-color: rgba(255, 77, 79, 0.1);
		color: #FF4D4F;
	}

	/* 弹窗样式 */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 999;
	}

	.user-modal, .delete-confirm-modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 650rpx;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		z-index: 1000;
		box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.1);
	}

	.user-modal {
		max-height: 950rpx;
		overflow-y: auto;
	}

	.modal-header {
		padding: 30rpx;
		border-bottom: 1rpx solid #F0F0F0;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.modal-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333333;
	}

	.modal-close {
		font-size: 40rpx;
		color: #999999;
		line-height: 1;
	}

	.modal-body {
		padding: 30rpx;
	}

	.form-group {
		margin-bottom: 24rpx;
	}

	.form-label {
		font-size: 26rpx;
		color: #666666;
		margin-bottom: 12rpx;
		display: block;
	}

	.form-group input, .form-group textarea {
		width: 100%;
		border: 1rpx solid #E8E8E8;
		border-radius: 8rpx;
		font-size: 26rpx;
		padding: 16rpx;
		box-sizing: border-box;
		color: #333333;
	}

	.form-group textarea {
		height: 160rpx;
	}

	.radio-group {
		display: flex;
		flex-wrap: wrap;
	}

	.radio-item {
		padding: 10rpx 24rpx;
		border: 1rpx solid #E8E8E8;
		border-radius: 8rpx;
		font-size: 24rpx;
		color: #666666;
		margin-right: 16rpx;
		margin-bottom: 16rpx;
	}

	.radio-item.active {
		border-color: #1890FF;
		background-color: rgba(24, 144, 255, 0.05);
		color: #1890FF;
	}

	.modal-footer {
		padding: 20rpx 30rpx 30rpx;
		display: flex;
		justify-content: flex-end;
		border-top: 1rpx solid #F0F0F0;
	}

	.modal-btn {
		padding: 16rpx 30rpx;
		border-radius: 8rpx;
		font-size: 26rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-left: 20rpx;
	}

	.cancel-btn {
		border: 1rpx solid #E8E8E8;
		color: #666666;
	}

	.confirm-btn {
		background-color: #1890FF;
		color: #FFFFFF;
	}

	/* 删除确认弹窗 */
	.delete-confirm-modal {
		width: 560rpx;
	}

	.confirm-text {
		font-size: 28rpx;
		color: #333333;
		margin-bottom: 16rpx;
		display: block;
	}

	.confirm-warning {
		font-size: 24rpx;
		color: #FF4D4F;
	}

	/* 空状态提示 */
	.empty-tip {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 60rpx 0;
		color: #999999;
		font-size: 26rpx;
	}

	.empty-icon {
		width: 48rpx;
		height: 48rpx;
		border-radius: 50%;
		background-color: #EEEEEE;
		color: #999999;
		display: flex;
		align-items: center;
		justify-content: center;
		font-style: italic;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	/* 新增样式 */
	.refresh-btn {
		height: 70rpx;
		background: linear-gradient(135deg, #13C2C2, #36CFC9);
		border-radius: 35rpx;
		color: #FFFFFF;
		font-size: 26rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(19, 194, 194, 0.2);
		margin-right: 20rpx;
	}
</style>
