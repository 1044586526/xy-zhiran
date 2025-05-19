<template>
	<view class="shenpi-container">
		<!-- 顶部区域 -->
		<view class="header-area">
			<view class="header">
				<view class="title-section">
					<text class="page-title">灾情救助审批</text>
					<text class="subtitle">审核编号: {{ applicationData.reportId || '暂无' }}</text>
				</view>
				
				<!-- 状态指示器 -->
				<view class="status-indicator" :class="'status-' + (applicationData.status || 0)">
					<text>{{ getStatusText(applicationData.status) }}</text>
				</view>
			</view>
			
			<!-- 审批进度条 -->
			<view class="approval-progress">
				<view class="progress-step" :class="{active: applicationData.status >= 0, completed: applicationData.status > 0}">
					<view class="step-dot"></view>
					<text class="step-text">提交申请</text>
				</view>
				<view class="progress-line" :class="{completed: applicationData.status > 0}"></view>
				
				<view class="progress-step" :class="{active: applicationData.status >= 1, completed: applicationData.status > 1}">
					<view class="step-dot"></view>
					<text class="step-text">审核中</text>
				</view>
				<view class="progress-line" :class="{completed: applicationData.status > 1}"></view>
				
				<view class="progress-step" :class="{
					active: applicationData.status >= 2, 
					completed: applicationData.status > 2,
					rejected: applicationData.status === 2
				}">
					<view class="step-dot"></view>
					<text class="step-text">{{ applicationData.status === 2 ? '已驳回' : '审核通过' }}</text>
				</view>
				<view class="progress-line" :class="{completed: applicationData.status > 3}"></view>
				
				<view class="progress-step" :class="{active: applicationData.status >= 4, completed: applicationData.status >= 4}">
					<view class="step-dot"></view>
					<text class="step-text">已发放</text>
				</view>
			</view>
		</view>
		
		<!-- 申请基本信息卡片 -->
		<view class="info-card">
			<view class="card-header">
				<text class="card-title">申请人信息</text>
			</view>
			
			<view class="applicant-summary">
				<!-- <view class="applicant-avatar">
					<text>{{ applicationData.name ? applicationData.name.substring(0, 1) : '?' }}</text>
				</view> -->
				<view class="applicant-info">
					<text class="applicant-name">{{ applicationData.name || '暂无' }}</text>
					<text class="applicant-details">{{ applicationData.phone || '暂无电话' }} | {{ formatDate(applicationData.createTime) }}</text>
				</view>
			</view>
			
			<view class="info-grid">
				<view class="info-item">
					<text class="label">身份证号</text>
					<text class="value">{{ desensitizeIDCard(applicationData.idCard) || '暂无' }}</text>
				</view>
				<view class="info-item">
					<text class="label">家庭人口</text>
					<text class="value">{{ applicationData.familySize || '0' }} 人</text>
				</view>
				<view class="info-item full-width">
					<text class="label">家庭住址</text>
					<text class="value">{{ applicationData.address || '暂无' }}</text>
				</view>
			</view>
		</view>
		
		<!-- 灾情信息卡片 -->
		<view class="info-card">
			<view class="card-header">
				<text class="card-title">灾情详情</text>
				<text class="disaster-date">发生日期: {{ applicationData.disasterDate || '暂无' }}</text>
			</view>
			
			<view class="disaster-type">
				<view class="disaster-tag">{{ applicationData.disasterType || '未指定灾害类型' }}</view>
			</view>
			
			<view class="info-list">
				<view class="info-row">
					<text class="label full-width">灾情描述</text>
					<text class="value description">{{ applicationData.disasterDesc || '暂无描述' }}</text>
				</view>
				
				<!-- 新增房屋损毁信息 -->
				<view class="info-row" v-if="applicationData.houseDamage">
					<text class="label full-width">房屋损毁程度</text>
					<text class="value">{{ applicationData.houseDamage }}</text>
				</view>
				
				<!-- 新增农作物受灾信息 -->
				<view class="info-row" v-if="applicationData.cropArea > 0">
					<text class="label full-width">农作物受灾面积</text>
					<text class="value">{{ applicationData.cropArea }} 亩</text>
				</view>
				
				<view class="losses-section">
					<view class="section-header">
						<text>损失情况</text>
						<text class="highlight">经济损失: {{ applicationData.estimatedLoss || '0' }} 元</text>
					</view>
					
					<view class="tags-container">
						<view class="tag" v-for="(type, index) in parseLossTypes(applicationData.lossTypes)" :key="'loss-'+index">
							{{ formatLossType(type) }}
						</view>
					</view>
					
					<text class="value description">{{ applicationData.lossDesc || '暂无详情' }}</text>
				</view>
			</view>
		</view>
		
		<!-- 救助需求卡片 -->
		<view class="info-card">
			<view class="card-header">
				<text class="card-title">救助需求</text>
				<!-- 新增申请救助金额 -->
				<text class="highlight" v-if="applicationData.requestAmount > 0">申请救助: {{ applicationData.requestAmount }} 元</text>
			</view>
			
			<view class="tags-container">
				<view class="tag relief-tag" v-for="(type, index) in parseReliefTypes(applicationData.reliefTypes)" :key="'relief-'+index">
					{{ formatReliefType(type) }}
				</view>
			</view>
			
			<!-- 物资需求 -->
			<view class="info-row" v-if="applicationData.materialNeeds">
				<text class="label full-width">物资需求</text>
				<text class="value description">{{ applicationData.materialNeeds }}</text>
			</view>
			
			<!-- 其他需求 -->
			<view class="info-row" v-if="applicationData.otherNeeds">
				<text class="label full-width">其他需求</text>
				<text class="value description">{{ applicationData.otherNeeds }}</text>
			</view>
		</view>
		
		<!-- 证明材料 -->
		<view class="info-card">
			<view class="card-header">
				<text class="card-title">证明材料</text>
				<text class="view-all" @click="viewAllEvidence" v-if="hasEvidence">查看全部</text>
			</view>
			
			<view class="evidence-list">
				<view v-if="!hasEvidence" class="empty-evidence">
					<text>申请人未提供证明材料</text>
				</view>
				
				<view v-else class="evidence-grid">
					<view class="evidence-item" v-for="(item, index) in parseEvidenceFiles(applicationData.evidenceFiles)" :key="index" @click="previewImage(item.url)">
						<image :src="item.url" mode="aspectFill"></image>
						<text class="evidence-name">{{ item.name || `材料${index + 1}` }}</text>
					</view>
				</view>
				
				<view class="evidence-desc" v-if="applicationData.evidenceDesc">
					<text class="label">材料说明</text>
					<text class="value">{{ applicationData.evidenceDesc }}</text>
				</view>
			</view>
		</view>
		
		<!-- 审批意见 -->
		<view class="info-card approval-card">
			<view class="card-header">
				<text class="card-title">审批意见</text>
			</view>
			
			<view class="opinion-section">
				<text class="approval-guide" v-if="applicationData.status <= 1">请详细填写审批意见，为后续决策提供依据</text>
				
				<textarea 
					class="opinion-input" 
					v-model="approvalOpinion" 
					placeholder="请输入审批意见..." 
					:disabled="applicationData.status > 1"
				></textarea>
				
				<!-- 快速填写选项 -->
				<view class="quick-options" v-if="applicationData.status <= 1">
					<text class="quick-option-label">快速填写:</text>
					<view class="options-container">
						<view class="quick-option" @click="setQuickOpinion('材料齐全，情况属实，符合救助条件，同意申请。')">
							同意(材料齐全)
						</view>
						<view class="quick-option" @click="setQuickOpinion('根据核查，受灾情况严重，建议优先救助。')">
							优先救助
						</view>
						<view class="quick-option" @click="setQuickOpinion('材料不完整，无法判断受灾情况，请补充提供相关证明。')">
							材料不完整
						</view>
						<view class="quick-option" @click="setQuickOpinion('经查证，情况与申报不符，不符合救助条件。')">
							情况不符
						</view>
					</view>
				</view>
				
				<!-- 历史审批意见 -->
				<view class="history-opinions" v-if="approvalHistory && approvalHistory.length > 0">
					<text class="section-title">历史审批记录</text>
					
					<view class="history-item" v-for="(item, index) in approvalHistory" :key="index">
						<view class="history-header">
							<text class="reviewer">{{ item.reviewer || '未知审批人' }}</text>
							<text class="history-time">{{ formatDate(item.approvalTime) }}</text>
							<text class="history-status" :class="'status-' + item.action">
								{{ getActionText(item.action) }}
							</text>
						</view>
						<text class="history-opinion">{{ item.opinion || '无审批意见' }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="action-bar" v-if="applicationData.status <= 1">
			<view class="action-btn reject-btn" @click="handleApproval('reject')">
				<text>驳回申请</text>
			</view>
			<view class="action-btn approve-btn" @click="handleApproval('approve')">
				<text>审批通过</text>
			</view>
		</view>
		
		<!-- 已完成审批提示 -->
		<view class="completed-tip" v-else>
			<text>该申请已{{ applicationData.status === 2 ? '驳回' : '审批通过' }}，无需进一步操作</text>
			<view class="return-btn" @click="goBack">返回列表</view>
		</view>
		
		<!-- 确认弹窗 -->
		<view class="modal-overlay" v-if="showConfirm" ></view>
		<view class="confirm-modal" v-if="showConfirm">
			<view class="modal-header">
				<text class="modal-title">确认{{ approvalAction === 'approve' ? '通过' : '驳回' }}</text>
			</view>
			
			<view class="modal-body">
				<text class="confirm-text">您确定要{{ approvalAction === 'approve' ? '通过' : '驳回' }}此申请吗？</text>
				<text class="confirm-warning" v-if="!approvalOpinion">请注意：您尚未填写审批意见。</text>
				
				<view class="opinion-preview" v-if="approvalOpinion">
					<text class="preview-label">审批意见预览：</text>
					<text class="preview-content">{{ approvalOpinion }}</text>
				</view>
			</view>
			
			<view class="modal-footer">
				<view class="modal-btn cancel-btn" @click="cancelApproval">
					<text>取消</text>
				</view>
				<view class="modal-btn confirm-btn" :class="approvalAction === 'approve' ? 'approve-btn' : 'reject-btn'" @click="confirmApproval">
					<text>确认{{ approvalAction === 'approve' ? '通过' : '驳回' }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
		import { baseUrl } from "@/utils/apiconfig.js";
	export default {
		data() {
			return {
				// 申请数据
				applicationData: {
					id: null,
					reportId: '',
					name: '',
					idCard: '',
					phone: '',
					address: '',
					familySize: 0,
					disasterType: '',
					disasterDate: '',
					disasterDesc: '',
					lossTypes: '',
					estimatedLoss: 0,
					lossDesc: '',
					reliefTypes: '',
					otherNeeds: '',
					materialNeeds: '',
					evidenceFiles: '',
					evidenceDesc: '',
					status: 0,
					createTime: null
				},
				// 审批意见
				approvalOpinion: '',
				// 审批历史
				approvalHistory: [],
				// 确认弹窗
				showConfirm: false,
				// 当前审批动作
				approvalAction: 'approve', // 'approve' 或 'reject'
				// 申请ID
				applicationId: null
			}
		},
		computed: {
			// 判断是否有证据材料
			hasEvidence() {
				return this.applicationData.evidenceFiles && 
					this.parseEvidenceFiles(this.applicationData.evidenceFiles).length > 0;
			}
		},
		onLoad(options) {
			// 从路由参数获取申请ID
			if (options && options.id) {
				this.applicationId = options.id;
				this.loadApplicationData();
			} else {
				uni.showToast({
					title: '未找到申请ID',
					icon: 'none'
				});
			}
		},
		methods: {
			// 返回列表
			goBack() {
				uni.navigateBack();
			},
			
			// 设置快速意见
			setQuickOpinion(opinion) {
				this.approvalOpinion = opinion;
			},
			
			// 查看所有证据
			viewAllEvidence() {
				const evidenceFiles = this.parseEvidenceFiles(this.applicationData.evidenceFiles);
				if (evidenceFiles.length > 0) {
					uni.previewImage({
						current: evidenceFiles[0].url,
						urls: evidenceFiles.map(item => item.url).filter(Boolean)
					});
				}
			},
			
			desensitizeIDCard(idCard) {
			  if (!idCard || idCard.length < 6) {
			    return '暂无';
			  }
			
			  // 中国大陆的身份证号码长度为18位
			  const idLength = idCard.length;
			  // 保留前6位和后4位，中间替换为星号
			  const prefix = idCard.substr(0, 4);
			  const suffix = idCard.substr(idLength - 4);
			  const middle = '*'.repeat(idLength - 8);
			
			  return prefix + middle + suffix;
			},
			
			// 加载申请数据
			loadApplicationData() {
				uni.showLoading({
					title: '加载中...'
				});
				
				const baseUrl = uni.getStorageSync('baseUrl') || '';
				if (!baseUrl) {
					uni.hideLoading();
					// uni.showToast({
					// 	title: 'API地址未配置',
					// 	icon: 'none'
					// });
					
					// 使用模拟数据进行测试
					this.loadMockData();
					return;
				}
				
				if (!this.applicationId) {
					uni.hideLoading();
					uni.showToast({
						title: '申请ID无效',
						icon: 'none'
					});
					return;
				}
				
				// 请求申请详情
				uni.request({
					url: `${baseUrl}/wxapi/shebao/list/${this.applicationId}`,
					method: 'GET',
					header: {
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						uni.hideLoading();
						
						console.log('申请详情响应:', res.data);
						
						// 检查响应
						if (res.statusCode === 200 && res.data.code === 200 && res.data.data) {
							// 处理返回的数据
							const data = res.data.data;
							
							// 更新申请数据 - 确保字段映射正确
							this.applicationData = {
								id: data.id,
								reportId: data.reportId || `申请${data.id}`,
								name: data.name || '未知申请人',
								idCard: data.idCard || '未知',
								phone: data.phone || '未知',
								address: data.address || '未知',
								familySize: data.familySize || 0,
								disasterType: this.formatDisasterType(data.disasterType) || '未知灾害',
								disasterDate: data.disasterDate || '未知日期',
								disasterDesc: data.disasterDesc || '无灾情描述',
								lossTypes: data.lossTypes || '',
								estimatedLoss: data.estimatedLoss || 0,
								lossDesc: data.lossDesc || '无损失详情',
								reliefTypes: data.reliefTypes || '',
								otherNeeds: data.otherNeeds || '',
								materialNeeds: data.materialNeeds || '',
								evidenceFiles: data.evidenceFiles || '',
								evidenceDesc: data.evidenceDesc || '',
								status: data.status !== undefined ? data.status : 0,
								createTime: data.createTime
							};
							
							// 如果有审核意见，则填入审批意见框
							if (data.auditRemark) {
								this.approvalOpinion = data.auditRemark;
							}
							
							// 加载审批历史
							this.loadApprovalHistory();
						} else {
							uni.showToast({
								title: res.data.msg || '获取数据失败',
								icon: 'none'
							});
							
							// 使用模拟数据
							this.loadMockData();
						}
					},
					fail: (err) => {
						uni.hideLoading();
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
						console.error('请求失败:', err);
						
						// 使用模拟数据
						this.loadMockData();
					}
				});
			},
			
			// 加载审批历史
			loadApprovalHistory() {
				// 从applicationData中提取审核信息创建审批历史
				if (this.applicationData.auditTime && this.applicationData.status > 1) {
					const action = this.applicationData.status === 2 ? 'reject' : 'approve';
					this.approvalHistory = [{
						reviewer: '系统审核员', // 没有审核人名称时使用默认值
						approvalTime: new Date(this.applicationData.auditTime).getTime(),
						action: action,
						opinion: this.applicationData.auditRemark || (action === 'approve' ? '审核通过' : '申请驳回')
					}];
				} else {
					this.approvalHistory = [];
				}
				
				// 真实接口调用可以在此添加
				// 目前使用从applicationData提取的审核信息
			},
			
			// 处理审批操作
			handleApproval(action) {
				console.log(111)
				this.approvalAction = action;
				this.showConfirm = true;
			},
			
			// 取消审批
			cancelApproval() {
				this.showConfirm = false;
			},
			
			// 确认审批
			confirmApproval() {
				if (!this.applicationId) {
					uni.showToast({
						title: '申请ID无效',
						icon: 'none'
					});
					return;
				}
				
				uni.showLoading({
					title: '提交中...'
				});
				
				if (!baseUrl) {
					uni.hideLoading();
					// uni.showToast({
					// 	title: 'API地址未配置',
					// 	icon: 'none'
					// });
					return;
				}
				
				// 根据具体需求准备提交数据
				const approvalData = {
					id: this.applicationId,
					status: this.approvalAction === 'approve' ? 1 : 2, // 3是审核通过，2是驳回
					auditRemark: this.approvalOpinion || (this.approvalAction === 'approve' ? '同意申请' : '申请不符合条件')
				};
				
				console.log('提交审批数据:', approvalData);
				
				
				// 发送审批请求
				uni.request({
					url: `${baseUrl}list/update`,
					method: 'POST',
					data: approvalData,
					header: {
						'content-type': 'application/json',
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						uni.hideLoading();
						
						if (res.statusCode === 200 && res.data.code === 200) {
							uni.showToast({
								title: '审批操作成功',
								icon: 'success'
							});
							
							// 关闭确认窗口
							this.showConfirm = false;
							
							// 更新申请状态
							this.applicationData.status = this.approvalAction === 'approve' ? 3 : 2;
							this.applicationData.auditRemark = this.approvalOpinion;
							this.applicationData.auditTime = new Date().toISOString().replace('T', ' ').substring(0, 19);
							
							// 添加到审批历史
							const newHistory = {
								reviewer: uni.getStorageSync('userName') || '当前审批人',
								approvalTime: new Date().getTime(),
								action: this.approvalAction === 'approve' ? 'approve' : 'reject',
								opinion: this.approvalOpinion
							};
							
							this.approvalHistory = [newHistory, ...this.approvalHistory];
							
							// 延迟返回
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
						} else {
							uni.showToast({
								title: res.data.msg || '审批操作失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						uni.hideLoading();
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
						console.error('审批请求失败:', err);
					}
				});
			},
			
			// 格式化损失类型并解析
			parseLossTypes(types) {
				if (!types) return [];
				
				try {
					const typesArray = JSON.parse(types);
					if (Array.isArray(typesArray)) {
						return typesArray;
					}
					return [types];
				} catch (e) {
					return [types];
				}
			},
			
			// 格式化显示损失类型
			formatLossType(type) {
				const typeMap = {
					'house': '房屋',
					'crop': '农作物',
					'belongings': '个人财产',
					'business': '商业损失',
					'livestock': '牲畜损失',
					'other': '其他损失'
				};
				return typeMap[type] || type;
			},
			
			// 解析救助类型
			parseReliefTypes(types) {
				if (!types) return [];
				
				try {
					const typesArray = JSON.parse(types);
					if (Array.isArray(typesArray)) {
						return typesArray;
					}
					return [types];
				} catch (e) {
					return [types];
				}
			},
			
			// 格式化显示救助类型
			formatReliefType(type) {
				const typeMap = {
					'fund': '资金救助',
					'material': '物资救助',
					'medical': '医疗救助',
					'housing': '住房救助',
					'psychological': '心理援助',
					'other': '其他救助'
				};
				return typeMap[type] || type;
			},
			
			// 解析证明材料
			parseEvidenceFiles(files) {
				if (!files) return [];
				
				try {
					return JSON.parse(files);
				} catch (e) {
					// 尝试检查是否为逗号分隔的URL列表
					if (typeof files === 'string') {
						return files.split(',').map((url, index) => ({
							url: url.trim(),
							name: `材料${index + 1}`
						}));
					}
					return [];
				}
			},
			
			// 预览图片
			previewImage(url) {
				if (!url) return;
				
				const evidenceFiles = this.parseEvidenceFiles(this.applicationData.evidenceFiles);
				const urls = evidenceFiles.map(item => item.url).filter(Boolean);
				
				uni.previewImage({
					current: url,
					urls: urls
				});
			},
			
			// 获取状态文本
			getStatusText(status) {
				const statusMap = {
					0: '待审核',
					1: '审核通过',
					2: '审核拒绝',
					3: '已发放',
				};
				return statusMap[status] || '未知状态';
			},
			
			// 获取操作文本
			getActionText(action) {
				const actionMap = {
					'approve': '通过',
					'reject': '驳回'
				};
				return actionMap[action] || action;
			},
			
			// 格式化日期
			formatDate(timestamp) {
				if (!timestamp) return '暂无记录';
				
				const date = new Date(timestamp);
				return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
			},
			
			// 格式化灾害类型
			formatDisasterType(type) {
				const typeMap = {
					'earthquake': '地震',
					'flood': '洪水',
					'typhoon': '台风',
					'drought': '干旱',
					'fire': '火灾',
					'landslide': '滑坡',
					'other': '其他灾害'
				};
				return typeMap[type] || type;
			},
			
			// 加载模拟数据（用于开发调试）
			loadMockData() {
				this.applicationData = {
					id: 1001,
					reportId: 'DR' + Date.now().toString().slice(-6),
					name: '张三',
					idCard: '110101199001010000',
					phone: '13800138000',
					address: '某省某市某区某街道123号',
					familySize: 4,
					disasterType: '洪水',
					disasterDate: '2023-07-15',
					disasterDesc: '由于持续强降雨，住宅一楼被淹，家具电器损坏严重，墙体受潮。',
					lossTypes: JSON.stringify(['房屋', '家具', '电器']),
					estimatedLoss: 50000,
					lossDesc: '冰箱、洗衣机、电视等电器损坏，无法使用；木质家具泡水变形；墙体地板需要修复。',
					reliefTypes: JSON.stringify(['物资', '资金']),
					otherNeeds: '需要临时住所安置',
					materialNeeds: '需要临时住所安置',
					evidenceFiles: JSON.stringify([
						{url: 'https://picsum.photos/300/200?random=1', name: '受灾房屋照片'},
						{url: 'https://picsum.photos/300/200?random=2', name: '损坏家具照片'},
						{url: 'https://picsum.photos/300/200?random=3', name: '家庭成员信息'}
					]),
					evidenceDesc: '提供了受灾现场照片和家庭基本情况说明',
					status: 1,
					createTime: Date.now() - 86400000 // 一天前
				};
				
				// 模拟审批历史
				this.approvalHistory = [
					{
						reviewer: '李审核',
						approvalTime: Date.now() - 43200000, // 12小时前
						action: 'approve',
						opinion: '初审通过，情况属实，建议予以救助支持。'
					}
				];
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F5F7FA;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	.shenpi-container {
		padding: 30rpx;
		padding-bottom: 150rpx;
	}

	/* 顶部区域样式 */
	.header-area {
		background: linear-gradient(135deg, #1890FF, #36CBCB);
		margin: -30rpx -30rpx 30rpx;
		padding: 50rpx 30rpx 30rpx;
		border-radius: 0 0 30rpx 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(24, 144, 255, 0.2);
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.title-section {
		flex: 1;
	}

	.page-title {
		font-size: 40rpx;
		font-weight: bold;
		color: #FFFFFF;
		margin-bottom: 8rpx;
		display: block;
	}

	.subtitle {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.8);
	}

	.status-indicator {
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		font-weight: bold;
		background-color: rgba(255, 255, 255, 0.3);
		color: #FFFFFF;
	}

	/* 审批进度条 */
	.approval-progress {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 0;
	}

	.progress-step {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;
		z-index: 2;
	}

	.step-dot {
		width: 24rpx;
		height: 24rpx;
		border-radius: 50%;
		background-color: rgba(255, 255, 255, 0.5);
		margin-bottom: 10rpx;
		transition: all 0.3s;
	}

	.step-text {
		font-size: 22rpx;
		color: rgba(255, 255, 255, 0.7);
		white-space: nowrap;
	}

	.progress-line {
		flex: 1;
		height: 4rpx;
		background-color: rgba(255, 255, 255, 0.3);
		margin: 0 5rpx;
		margin-bottom: 34rpx;
		position: relative;
		z-index: 1;
	}

	.progress-step.active .step-dot {
		background-color: #FFFFFF;
		box-shadow: 0 0 10rpx rgba(255, 255, 255, 0.5);
	}

	.progress-step.active .step-text {
		color: #FFFFFF;
		font-weight: bold;
	}

	.progress-step.completed .step-dot {
		background-color: #67C23A;
		box-shadow: 0 0 10rpx rgba(103, 194, 58, 0.5);
	}

	.progress-line.completed {
		background-color: #67C23A;
	}

	.progress-step.rejected .step-dot {
		background-color: #F56C6C;
		box-shadow: 0 0 10rpx rgba(245, 108, 108, 0.5);
	}

	/* 卡片样式 */
	.info-card {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #F0F0F0;
	}

	.card-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333333;
	}

	.application-id {
		font-size: 24rpx;
		color: #909399;
	}

	/* 基本信息网格 */
	.info-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.info-item {
		display: flex;
		flex-direction: column;
	}

	.label {
		font-size: 24rpx;
		color: #909399;
		margin-bottom: 8rpx;
	}

	.value {
		font-size: 28rpx;
		color: #333333;
		word-break: break-all;
	}

	.highlight {
		color: #F56C6C;
		font-weight: bold;
	}

	/* 信息列表样式 */
	.info-list {
		display: flex;
		flex-direction: column;
	}

	.info-row {
		margin-bottom: 20rpx;
		display: flex;
		flex-direction: column;
	}

	.info-row:last-child {
		margin-bottom: 0;
	}

	.full-width {
		display: block;
		margin-bottom: 10rpx;
	}

	.description {
		background-color: #F5F7FA;
		padding: 16rpx;
		border-radius: 8rpx;
		line-height: 1.5;
	}

	/* 证明材料样式 */
	.evidence-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
		margin-bottom: 20rpx;
	}

	.evidence-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.evidence-item image {
		width: 180rpx;
		height: 140rpx;
		border-radius: 8rpx;
		margin-bottom: 10rpx;
		border: 1rpx solid #EBEEF5;
	}

	.evidence-name {
		font-size: 22rpx;
		color: #606266;
		text-align: center;
		width: 100%;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.empty-evidence {
		padding: 40rpx 0;
		text-align: center;
		color: #909399;
		font-size: 26rpx;
	}

	.evidence-desc {
		margin-top: 20rpx;
		padding-top: 20rpx;
		border-top: 1rpx dashed #EBEEF5;
	}

	/* 审批意见样式 */
	.opinion-section {
		display: flex;
		flex-direction: column;
	}

	.opinion-input {
		width: 100%;
		height: 200rpx;
		padding: 20rpx;
		font-size: 28rpx;
		border: 1rpx solid #EBEEF5;
		border-radius: 8rpx;
		margin-bottom: 30rpx;
		box-sizing: border-box;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 20rpx;
		display: block;
	}

	.history-opinions {
		display: flex;
		flex-direction: column;
	}

	.history-item {
		border-left: 4rpx solid #409EFF;
		padding-left: 20rpx;
		margin-bottom: 30rpx;
	}

	.history-header {
		display: flex;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.reviewer {
		font-size: 26rpx;
		font-weight: bold;
		color: #333333;
		margin-right: 20rpx;
	}

	.history-time {
		font-size: 22rpx;
		color: #909399;
		flex: 1;
	}

	.history-status {
		font-size: 22rpx;
		padding: 4rpx 12rpx;
		border-radius: 10rpx;
	}

	.history-opinion {
		font-size: 26rpx;
		color: #606266;
		line-height: 1.5;
	}

	/* 底部操作栏 */
	.action-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		justify-content: space-around;
		padding: 20rpx 30rpx;
		background-color: #FFFFFF;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.action-btn {
		flex: 1;
		height: 80rpx;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: bold;
		margin: 0 20rpx;
	}

	.reject-btn {
		background-color: #FEF0F0;
		color: #F56C6C;
		border: 1rpx solid #FBC4C4;
	}

	.approve-btn {
		background-color: #F0F9EB;
		color: #67C23A;
		border: 1rpx solid #C2E7B0;
	}

	/* 完成审批提示 */
	.completed-tip {
		padding: 20rpx;
		text-align: center;
		color: #909399;
		font-size: 26rpx;
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
	
.confirm-modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 650rpx;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		z-index: 1000;
		box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.1);
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
		
	}
</style>
