<template>
	<view class="report-container">
		<!-- 页面标题 -->
		<view class="page-header">
			<view class="header-title">
				<text class="title-main">救助申报</text>
				<text class="title-sub">Disaster Relief Application</text>
			</view>
		</view>
		
		<!-- 表单区域 -->
		<view class="form-container">
			<!-- 基本信息 -->
			<view class="form-section">
				<view class="section-title">
					<text class="section-icon cuIcon-profile"></text>
					<text class="section-text">基本信息</text>
				</view>
				
				<view class="form-item">
					<text class="label required">申报人姓名</text>
					<input class="input" type="text" v-model="formData.name" placeholder="请输入申报人真实姓名" />
				</view>
				
				<view class="form-item">
					<text class="label required">身份证号码</text>
					<input class="input" type="idcard" v-model="formData.idCard" placeholder="请输入18位身份证号码" maxlength="18" />
				</view>
				
				<view class="form-item">
					<text class="label required">联系电话</text>
					<input class="input" type="number" v-model="formData.phone" placeholder="请输入联系电话" maxlength="11" />
				</view>
				
				<view class="form-item">
					<text class="label required">家庭住址</text>
					<input class="input" type="text" v-model="formData.address" placeholder="请输入详细家庭住址" />
				</view>
				
				<view class="form-item">
					<text class="label required">家庭人口数</text>
					<input class="input" type="number" v-model="formData.familySize" placeholder="请输入家庭人口数" />
				</view>
			</view>
			
			<!-- 灾情信息 -->
			<view class="form-section">
				<view class="section-title">
					<text class="section-icon cuIcon-warn"></text>
					<text class="section-text">灾情信息</text>
				</view>
				
				<view class="form-item">
					<text class="label required">灾害类型</text>
					<picker class="picker" mode="selector" :range="disasterTypeNames" @change="onDisasterTypeChange">
						<view class="picker-text">
							<text v-if="formData.disasterType">{{getDisasterTypeName(formData.disasterType)}}</text>
							<text v-else class="placeholder">请选择灾害类型</text>
						</view>
					</picker>
				</view>
				
				<view class="form-item">
					<text class="label required">灾害发生时间</text>
					<picker class="picker" mode="date" @change="onDisasterDateChange">
						<view class="picker-text">
							<text v-if="formData.disasterDate">{{formData.disasterDate}}</text>
							<text v-else class="placeholder">请选择灾害发生时间</text>
						</view>
					</picker>
				</view>
				
				<view class="form-item">
					<text class="label required">灾害详情描述</text>
					<textarea class="textarea" v-model="formData.disasterDesc" placeholder="请详细描述灾害发生情况、影响范围等" />
				</view>
			</view>
			
			<!-- 损失情况 -->
			<view class="form-section">
				<view class="section-title">
					<text class="section-icon cuIcon-moneybag"></text>
					<text class="section-text">损失情况</text>
				</view>
				
				<view class="form-item">
					<text class="label">损失类型（可多选）</text>
					<view class="type-grid">
						<view class="type-item" 
							v-for="(item, index) in lossTypes" 
							:key="index"
							:class="{'type-active': formData.lossTypeList.includes(item.value)}"
							@tap="toggleLossType(item.value)">
							<text class="type-icon" :class="item.icon"></text>
							<text class="type-text">{{item.text}}</text>
						</view>
					</view>
				</view>
				
				<view class="form-item" v-if="formData.lossTypeList.includes('house')">
					<text class="label">房屋损失情况</text>
					<picker class="picker" mode="selector" :range="houseDamageNames" @change="onHouseDamageChange">
						<view class="picker-text">
							<text v-if="formData.houseDamage">{{getHouseDamageName(formData.houseDamage)}}</text>
							<text v-else class="placeholder">请选择房屋损失情况</text>
						</view>
					</picker>
				</view>
				
				<view class="form-item" v-if="formData.lossTypeList.includes('crop')">
					<text class="label">农作物受灾面积（亩）</text>
					<input class="input" type="digit" v-model="formData.cropArea" placeholder="请输入受灾面积" />
				</view>
				
				<view class="form-item">
					<text class="label required">预估经济损失（元）</text>
					<input class="input" type="digit" v-model="formData.estimatedLoss" placeholder="请输入预估经济损失金额" />
				</view>
				
				<view class="form-item">
					<text class="label">损失详细说明</text>
					<textarea class="textarea" v-model="formData.lossDesc" placeholder="请详细说明各项损失情况" />
				</view>
			</view>
			
			<!-- 救助需求 -->
			<view class="form-section">
				<view class="section-title">
					<text class="section-icon cuIcon-activity"></text>
					<text class="section-text">救助需求</text>
				</view>
				
				<view class="form-item">
					<text class="label">救助类型（可多选）</text>
					<view class="type-grid">
						<view class="type-item" 
							v-for="(item, index) in reliefTypes" 
							:key="index"
							:class="{'type-active': formData.reliefTypeList.includes(item.value)}"
							@tap="toggleReliefType(item.value)">
							<text class="type-icon" :class="item.icon"></text>
							<text class="type-text">{{item.text}}</text>
						</view>
					</view>
				</view>
				
				<view class="form-item" v-if="formData.reliefTypeList.includes('fund')">
					<text class="label">申请救助金额（元）</text>
					<input class="input" type="digit" v-model="formData.requestAmount" placeholder="请输入申请救助金额" />
				</view>
				
				<view class="form-item" v-if="formData.reliefTypeList.includes('material')">
					<text class="label">物资需求</text>
					<textarea class="textarea" v-model="formData.materialNeeds" placeholder="请详细说明需要的物资种类、数量等" />
				</view>
				
				<view class="form-item">
					<text class="label">其他救助需求</text>
					<textarea class="textarea" v-model="formData.otherNeeds" placeholder="请说明其他救助需求" />
				</view>
			</view>
			
			<!-- 佐证材料 -->
			<view class="form-section">
				<view class="section-title">
					<text class="section-icon cuIcon-pic"></text>
					<text class="section-text">佐证材料</text>
					<text class="upload-tip">（请上传受灾照片、损失证明等）</text>
				</view>
				
				<view class="upload-box">
					<view class="upload-list">
						<view class="upload-item" v-for="(item, index) in formData.evidenceFiles" :key="index">
							<image class="upload-image" :src="item.url" mode="aspectFill"></image>
							<view class="upload-delete" @tap="removeFile(index)">
								<text class="cuIcon-close"></text>
							</view>
							<view class="upload-name">{{item.name}}</view>
						</view>
						
						<view class="upload-add" @tap="chooseFile" v-if="formData.evidenceFiles.length < 9">
							<text class="upload-add-icon cuIcon-cameraadd"></text>
							<text class="upload-add-text">添加图片</text>
						</view>
					</view>
				</view>
				
				<view class="form-item">
					<text class="label">材料说明</text>
					<textarea class="textarea" v-model="formData.evidenceDesc" placeholder="请对上传的佐证材料进行说明" />
				</view>
			</view>
			
			<!-- 承诺声明 -->
			<view class="form-section">
				<view class="declaration" @tap="toggleDeclaration">
					<view class="custom-checkbox">
						<view class="checkbox-inner" :class="{'checked': formData.declaration}"></view>
					</view>
					<text class="declaration-text">
						我承诺以上信息真实有效，如有虚假信息愿承担相应法律责任
					</text>
				</view>
			</view>
			
			<!-- 提交按钮 -->
			<view class="btn-group">
				<!-- <button class="btn-preview" @tap="previewForm">预览申报</button> -->
				<button class="btn-submit" :disabled="!formData.declaration" @tap="submitForm">提交申报</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				formData: {
					// 基本信息
					name: '',
					idCard: '',
					phone: '',
					address: '',
					familySize: '',
					
					// 灾情信息
					disasterType: '',
					disasterDate: '',
					disasterDesc: '',
					
					// 损失情况
					lossTypeList: [],
					houseDamage: '',
					cropArea: '',
					estimatedLoss: '',
					lossDesc: '',
					
					// 救助需求
					reliefTypeList: [],
					requestAmount: '',
					materialNeeds: '',
					otherNeeds: '',
					
					// 佐证材料
					evidenceFiles: [],
					evidenceDesc: '',
					
					// 承诺声明
					declaration: false
				},
				disasterTypes: [
					{ value: 'flood', text: '洪涝灾害' },
					{ value: 'earthquake', text: '地震灾害' },
					{ value: 'drought', text: '干旱灾害' },
					{ value: 'typhoon', text: '台风灾害' },
					{ value: 'landslide', text: '滑坡灾害' },
					{ value: 'fire', text: '火灾' },
					{ value: 'other', text: '其他灾害' }
				],
				houseDamageTypes: [
					{ value: 'collapse', text: '完全倒塌' },
					{ value: 'severe', text: '严重损坏（不能居住）' },
					{ value: 'moderate', text: '中度损坏（可修复）' },
					{ value: 'slight', text: '轻微损坏' }
				],
				lossTypes: [
					{ value: 'house', text: '房屋损失', icon: 'cuIcon-home' },
					{ value: 'crop', text: '农作物损失', icon: 'cuIcon-shop' },
					{ value: 'livestock', text: '牲畜损失', icon: 'cuIcon-rankfill' },
					{ value: 'belongings', text: '财物损失', icon: 'cuIcon-furniture' },
					{ value: 'business', text: '经营损失', icon: 'cuIcon-shopfill' },
					{ value: 'other', text: '其他损失', icon: 'cuIcon-more' }
				],
				reliefTypes: [
					{ value: 'fund', text: '资金救助', icon: 'cuIcon-moneybag' },
					{ value: 'material', text: '物资救助', icon: 'cuIcon-present' },
					{ value: 'housing', text: '住房救助', icon: 'cuIcon-home' },
					{ value: 'medical', text: '医疗救助', icon: 'cuIcon-medal' },
					{ value: 'employment', text: '就业援助', icon: 'cuIcon-people' },
					{ value: 'other', text: '其他援助', icon: 'cuIcon-more' }
				]
			}
		},
		computed: {
			disasterTypeNames() {
				return this.disasterTypes.map(item => item.text);
			},
			houseDamageNames() {
				return this.houseDamageTypes.map(item => item.text);
			}
		},
		methods: {
			// 灾害类型选择
			onDisasterTypeChange(e) {
				const index = e.detail.value;
				this.formData.disasterType = this.disasterTypes[index].value;
			},
			
			// 获取灾害类型名称
			getDisasterTypeName(value) {
				const item = this.disasterTypes.find(item => item.value === value);
				return item ? item.text : '';
			},
			
			// 灾害日期选择
			onDisasterDateChange(e) {
				this.formData.disasterDate = e.detail.value;
			},
			
			// 房屋损失程度选择
			onHouseDamageChange(e) {
				const index = e.detail.value;
				this.formData.houseDamage = this.houseDamageTypes[index].value;
			},
			
			// 获取房屋损失程度名称
			getHouseDamageName(value) {
				const item = this.houseDamageTypes.find(item => item.value === value);
				return item ? item.text : '';
			},
			
			// 选择/取消选择损失类型
			toggleLossType(value) {
				const index = this.formData.lossTypeList.indexOf(value);
				if (index === -1) {
					this.formData.lossTypeList.push(value);
				} else {
					this.formData.lossTypeList.splice(index, 1);
				}
			},
			
			// 选择/取消选择救助类型
			toggleReliefType(value) {
				const index = this.formData.reliefTypeList.indexOf(value);
				if (index === -1) {
					this.formData.reliefTypeList.push(value);
				} else {
					this.formData.reliefTypeList.splice(index, 1);
				}
			},
			
			// 选择文件（图片）
			chooseFile() {
				uni.chooseImage({
					count: 9 - this.formData.evidenceFiles.length,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFiles = res.tempFiles;
						tempFiles.forEach(file => {
							this.formData.evidenceFiles.push({
								url: file.path,
								name: file.name || '佐证图片' + (this.formData.evidenceFiles.length + 1),
								size: file.size
							});
						});
					}
				});
			},
			
			// 移除文件
			removeFile(index) {
				this.formData.evidenceFiles.splice(index, 1);
			},
			
			// 切换承诺声明
			toggleDeclaration() {
				this.formData.declaration = !this.formData.declaration;
			},
			
			// 表单验证
			validateForm() {
				// 基本信息验证
				if (!this.formData.name) {
					uni.showToast({
						title: '请输入申报人姓名',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.idCard) {
					uni.showToast({
						title: '请输入身份证号码',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.phone) {
					uni.showToast({
						title: '请输入联系电话',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.address) {
					uni.showToast({
						title: '请输入家庭住址',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.familySize) {
					uni.showToast({
						title: '请输入家庭人口数',
						icon: 'none'
					});
					return false;
				}
				
				// 灾情信息验证
				if (!this.formData.disasterType) {
					uni.showToast({
						title: '请选择灾害类型',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.disasterDate) {
					uni.showToast({
						title: '请选择灾害发生时间',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.formData.disasterDesc) {
					uni.showToast({
						title: '请描述灾害详情',
						icon: 'none'
					});
					return false;
				}
				
				// 损失情况验证
				if (!this.formData.estimatedLoss) {
					uni.showToast({
						title: '请输入预估经济损失',
						icon: 'none'
					});
					return false;
				}
				
				return true;
			},
			
			// 预览申报内容
			previewForm() {
				if (!this.validateForm()) return;
				
				uni.showToast({
					title: '预览功能开发中',
					icon: 'none'
				});
			},
			
			// 提交申报
			submitForm() {
				if (!this.formData.declaration) {
					uni.showToast({
						title: '请先同意承诺声明',
						icon: 'none'
					});
					return;
				}
				
				if (!this.validateForm()) return;
				
				// 显示加载中
				uni.showLoading({
					title: '提交中...'
				});
				
				// 准备提交数据
				const reportData = {
					// 基本信息
					name: this.formData.name,
					idCard: this.formData.idCard,
					phone: this.formData.phone,
					address: this.formData.address,
					familySize: this.formData.familySize,
					
					// 灾情信息
					disasterType: this.formData.disasterType,
					disasterDate: this.formData.disasterDate,
					disasterDesc: this.formData.disasterDesc,
					
					// 损失情况
					lossTypes: JSON.stringify(this.formData.lossTypeList),
					houseDamage: this.formData.houseDamage,
					cropArea: this.formData.cropArea,
					estimatedLoss: this.formData.estimatedLoss,
					lossDesc: this.formData.lossDesc,
					
					// 救助需求
					reliefTypes: JSON.stringify(this.formData.reliefTypeList),
					requestAmount: this.formData.requestAmount,
					materialNeeds: this.formData.materialNeeds,
					otherNeeds: this.formData.otherNeeds,
					
					// 佐证材料
					evidenceFiles: JSON.stringify(this.formData.evidenceFiles),
					evidenceDesc: this.formData.evidenceDesc,
					
					// 声明确认
					declaration: this.formData.declaration ? 1 : 0,
					
					// 初始状态
					status: 0
				};
				
				// 发送请求到后端接口
				uni.request({
					url: 'http://localhost:8083/wxapi/shebao',
					method: 'POST', // 改为POST方法
					data: reportData,
					header: {
						'Content-Type': 'application/json' // 修改为JSON格式
					},
					success: (res) => {
						uni.hideLoading();
						
						if (res.data.code === 200) {
							// 提交成功
							uni.showModal({
								title: '提交成功',
								content: res.data.msg || '您的救助申报已提交成功，请等待管理员审核',
								showCancel: false,
								success: () => {
									// 修改为跳转回首页
									uni.reLaunch({
										url: '/pages/index/index'
									});
								}
							});
						} else {
							// 提交失败
							uni.showModal({
								title: '提交失败',
								content: res.data.msg || '服务器异常，请稍后重试',
								showCancel: false
							});
						}
					},
					fail: (err) => {
						uni.hideLoading();
						console.error('提交失败', err);
						uni.showModal({
							title: '提交失败',
							content: '网络异常，请检查网络连接后重试',
							showCancel: false
						});
					}
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
	
	.report-container {
		padding-bottom: 40rpx;
	}
	
	/* 页面标题 */
	.page-header {
		background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
		padding: 60rpx 40rpx;
		color: #fff;
	}
	
	.header-title {
		display: flex;
		flex-direction: column;
	}
	
	.title-main {
		font-size: 44rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}
	
	.title-sub {
		font-size: 28rpx;
		opacity: 0.8;
	}
	
	/* 表单容器 */
	.form-container {
		padding: 30rpx;
	}
	
	/* 表单分节 */
	.form-section {
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
	}
	
	.section-title {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
		border-bottom: 1px solid #f0f0f0;
		padding-bottom: 20rpx;
	}
	
	.section-icon {
		color: #4facfe;
		font-size: 36rpx;
		margin-right: 16rpx;
	}
	
	.section-text {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.upload-tip {
		font-size: 24rpx;
		color: #999;
		margin-left: 16rpx;
	}
	
	/* 表单项样式 */
	.form-item {
		margin-bottom: 30rpx;
	}
	
	.label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 12rpx;
	}
	
	.required:after {
		content: '*';
		color: #ff6b6b;
		margin-left: 4rpx;
	}
	
	.input {
		width: 100%;
		height: 90rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
	}
	
	.textarea {
		width: 100%;
		height: 200rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
	}
	
	.picker {
		width: 100%;
	}
	
	.picker-text {
		width: 100%;
		height: 90rpx;
		line-height: 90rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
	}
	
	.placeholder {
		color: #999;
	}
	
	/* 类型选择网格 */
	.type-grid {
		display: flex;
		flex-wrap: wrap;
		margin: 0 -10rpx;
	}
	
	.type-item {
		width: calc(33.33% - 20rpx);
		height: 160rpx;
		margin: 10rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.type-active {
		background: rgba(79, 172, 254, 0.1);
		border: 2rpx solid #4facfe;
	}
	
	.type-icon {
		font-size: 48rpx;
		color: #666;
		margin-bottom: 16rpx;
	}
	
	.type-active .type-icon {
		color: #4facfe;
	}
	
	.type-text {
		font-size: 28rpx;
		color: #666;
	}
	
	.type-active .type-text {
		color: #4facfe;
		font-weight: bold;
	}
	
	/* 上传文件 */
	.upload-box {
		margin-bottom: 30rpx;
	}
	
	.upload-list {
		display: flex;
		flex-wrap: wrap;
		margin: 0 -10rpx;
	}
	
	.upload-item {
		width: calc(33.33% - 20rpx);
		height: 220rpx;
		margin: 10rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		position: relative;
		overflow: hidden;
	}
	
	.upload-image {
		width: 100%;
		height: 180rpx;
		object-fit: cover;
	}
	
	.upload-delete {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		width: 40rpx;
		height: 40rpx;
		background: rgba(0, 0, 0, 0.6);
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 1;
	}
	
	.upload-delete text {
		color: #fff;
		font-size: 24rpx;
	}
	
	.upload-name {
		height: 40rpx;
		line-height: 40rpx;
		padding: 0 10rpx;
		font-size: 22rpx;
		color: #666;
		text-align: center;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	
	.upload-add {
		width: calc(33.33% - 20rpx);
		height: 220rpx;
		margin: 10rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border: 2rpx dashed #ddd;
	}
	
	.upload-add-icon {
		font-size: 60rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	.upload-add-text {
		font-size: 26rpx;
		color: #999;
	}
	
	/* 自定义复选框 */
	.declaration {
		display: flex;
		align-items: flex-start;
		padding: 10rpx 0;
	}
	
	.custom-checkbox {
		width: 40rpx;
		height: 40rpx;
		border: 2rpx solid #ddd;
		border-radius: 6rpx;
		margin-right: 16rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-shrink: 0;
		margin-top: 4rpx;
	}
	
	.checkbox-inner {
		width: 24rpx;
		height: 24rpx;
		border-radius: 4rpx;
		background: transparent;
	}
	
	.checkbox-inner.checked {
		background: #4facfe;
	}
	
	.declaration-text {
		flex: 1;
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
	}
	
	/* 提交按钮 */
	.btn-group {
		display: flex;
		justify-content: space-between;
		margin-top: 50rpx;
		padding: 0 20rpx;
	}
	
	.btn-preview, .btn-submit {
		width: 45%;
		height: 90rpx;
		line-height: 90rpx;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: bold;
	}
	
	.btn-preview {
		background: #fff;
		color: #4facfe;
		border: 1px solid #4facfe;
	}
	
	.btn-submit {
		background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
		color: #fff;
		box-shadow: 0 6rpx 20rpx rgba(79, 172, 254, 0.3);
	}
	
	.btn-submit:disabled {
		background: #cccccc;
		box-shadow: none;
		color: #fff;
	}
</style> 