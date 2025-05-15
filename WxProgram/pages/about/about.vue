<!-- 关于作者 -->
<template>
  <view class="help-container">
    <!-- 页面标题区域 -->
    <view class="page-header">
      <view class="header-title">
        <text class="title-main">使用帮助</text>
        <text class="title-sub">Help & FAQ</text>
      </view>
    </view>
    
    <!-- 内容区域 -->
    <view class="content-wrapper">
      <!-- 标签页切换 -->
      <view class="tabs">
        <view 
          class="tab-item" 
          v-for="(tab, index) in tabs" 
          :key="index" 
          :class="{ active: currentTab === index }"
          @tap="switchTab(index)"
        >
          <text class="tab-icon" :class="tab.icon"></text>
          <text class="tab-text">{{ tab.name }}</text>
        </view>
      </view>
      
      <!-- 使用指南 -->
      <view v-if="currentTab === 0" class="tab-content guide-content">
        <view class="guide-section" v-for="(section, sectionIndex) in guideData" :key="sectionIndex">
          <view class="section-header" @tap="toggleSection(sectionIndex)">
            <text class="section-title">{{ section.title }}</text>
            <text class="section-icon" :class="section.expanded ? 'cuIcon-unfold' : 'cuIcon-right'"></text>
          </view>
          
          <view class="section-content" v-if="section.expanded">
            <view class="guide-item" v-for="(item, itemIndex) in section.items" :key="itemIndex">
              <view class="guide-step">
                <text class="step-number">{{ itemIndex + 1 }}</text>
                <text class="step-title">{{ item.title }}</text>
              </view>
              <text class="step-desc">{{ item.desc }}</text>
              <image 
                v-if="item.image" 
                class="step-image" 
                :src="item.image" 
                mode="widthFix" 
                @tap="previewImage(item.image)"
              ></image>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 常见问题 -->
      <view v-if="currentTab === 1" class="tab-content faq-content">
        <view class="faq-item" v-for="(faq, index) in faqData" :key="index" @tap="toggleFaq(index)">
          <view class="faq-question">
            <text class="question-icon cuIcon-question"></text>
            <text class="question-text">{{ faq.question }}</text>
            <text class="question-arrow" :class="faq.expanded ? 'cuIcon-fold' : 'cuIcon-unfold'"></text>
          </view>
          
          <view class="faq-answer" v-if="faq.expanded">
            <text class="answer-text">{{ faq.answer }}</text>
          </view>
        </view>
      </view>
      
      <!-- 用户留言 -->
      <view v-if="currentTab === 2" class="tab-content feedback-content">
        <!-- 留言表单 -->
        <view class="feedback-form">
          <view class="form-header">
            <text class="form-title">您的建议和问题</text>
            <text class="form-subtitle">提交您的问题或建议，我们会尽快回复</text>
          </view>
          
          <view class="form-item">
            <textarea 
              class="feedback-input" 
              v-model="feedbackForm.content" 
              placeholder="请详细描述您的问题或建议..."
              maxlength="500"
            ></textarea>
            <text class="input-count">{{ feedbackForm.content.length }}/500</text>
          </view>
          
          <view class="form-item">
            <input 
              class="contact-input" 
              v-model="feedbackForm.contact" 
              placeholder="留下您的联系方式（选填）"
            />
          </view>
          
          <view class="form-item">
            <button class="submit-btn" @tap="submitFeedback">提交留言</button>
          </view>
        </view>
        
        <!-- 留言列表 -->
        <view class="feedback-list">
          <view class="list-header">
            <text class="list-title">历史留言</text>
            <text class="list-count">(共{{ feedbackList.length }}条)</text>
          </view>
          
          <view v-if="feedbackList.length === 0" class="empty-feedback">
            <text class="cuIcon-info empty-icon"></text>
            <text class="empty-text">暂无留言</text>
          </view>
          
          <view v-else class="feedback-items">
            <view class="feedback-item" v-for="(item, index) in feedbackList" :key="index">
              <view class="item-header">
                <text class="user-name">{{ item.userName }}</text>
                <text class="item-time">{{ item.time }}</text>
              </view>
              <text class="item-content">{{ item.content }}</text>
              
              <view class="reply-box" v-if="item.reply">
                <text class="reply-label">官方回复：</text>
                <text class="reply-content">{{ item.reply }}</text>
              </view>
		</view>
	</view>
	
          <view class="load-more" v-if="hasMoreFeedback" @tap="loadMoreFeedback">
            <text class="more-text">加载更多</text>
          </view>
        </view>
		</view>
	</view>
    
    <!-- 底部联系方式 -->
    <view class="contact-footer">
      <view class="contact-item" @tap="makePhoneCall">
        <text class="cuIcon-service"></text>
        <text class="contact-text">客服热线: 0123-4567890</text>
      </view>
      <text class="copyright">灾情救助平台 © 2023 版权所有</text>
    </view>
  </view>
</template>

<script>
  export default {
    data() {
      return {
        currentTab: 0,
        tabs: [
          { name: '使用指南', icon: 'cuIcon-formfill' },
          { name: '常见问题', icon: 'cuIcon-questionfill' },
          { name: '留言反馈', icon: 'cuIcon-comment' }
        ],
        guideData: [
          {
            title: '如何申报灾情救助',
            expanded: true,
            items: [
              {
                title: '进入救助申报页面',
                desc: '在首页点击"救助申报"按钮，进入申报页面。',
                image: 'https://cdn.uviewui.com/uview/swiper/1.jpg'
              },
              {
                title: '填写基本信息',
                desc: '按照要求填写个人基本信息，包括姓名、身份证号、联系方式等。',
                image: 'https://cdn.uviewui.com/uview/swiper/2.jpg'
              },
              {
                title: '描述灾情损失',
                desc: '详细描述灾害类型、发生时间、造成的损失情况等。',
                image: 'https://cdn.uviewui.com/uview/swiper/3.jpg'
              },
              {
                title: '上传佐证材料',
                desc: '上传灾情照片等证明材料，以便管理员审核。',
                image: 'https://cdn.uviewui.com/uview/swiper/1.jpg'
              },
              {
                title: '提交申报',
                desc: '确认信息无误后，勾选承诺声明，点击"提交申报"按钮完成申报。',
                image: 'https://cdn.uviewui.com/uview/swiper/2.jpg'
              }
            ]
          },
          {
            title: '如何查询申报进度',
            expanded: false,
            items: [
              {
                title: '进入申报查询页面',
                desc: '在首页点击"申报查询"按钮，进入查询页面。',
                image: 'https://cdn.uviewui.com/uview/swiper/3.jpg'
              },
              {
                title: '输入查询信息',
                desc: '输入身份证号、手机号或申报编号进行查询。',
                image: 'https://cdn.uviewui.com/uview/swiper/1.jpg'
              },
              {
                title: '查看申报状态',
                desc: '可以查看申报的处理状态、审核进度等信息。',
                image: 'https://cdn.uviewui.com/uview/swiper/2.jpg'
              }
            ]
          },
          {
            title: '如何查看救助信息公示',
            expanded: false,
            items: [
              {
                title: '进入公示查询页面',
                desc: '在首页点击"公示查询"按钮，进入公示信息页面。',
                image: 'https://cdn.uviewui.com/uview/swiper/3.jpg'
              },
              {
                title: '筛选查询条件',
                desc: '可以按灾害类型、区域、时间等条件筛选公示信息。',
                image: 'https://cdn.uviewui.com/uview/swiper/1.jpg'
              },
              {
                title: '查看详细信息',
                desc: '点击公示信息可以查看详细的救助内容和发放情况。',
                image: 'https://cdn.uviewui.com/uview/swiper/2.jpg'
              }
            ]
          }
        ],
        faqData: [
          {
            question: '申报救助需要哪些材料？',
            answer: '申报救助需要提供：1. 个人身份证明（身份证）；2. 受灾证明材料（照片、视频等）；3. 损失情况说明；4. 联系方式。如有特殊情况，可能需要提供其他补充材料。',
            expanded: false
          },
          {
            question: '救助申报多久能审核通过？',
            answer: '一般情况下，救助申报会在3-5个工作日内完成审核。特殊情况或重大灾害期间，可能需要更长时间，请耐心等待。',
            expanded: false
          },
          {
            question: '救助金额是如何确定的？',
            answer: '救助金额根据灾害类型、受灾程度、实际损失等多种因素综合评估确定。评估标准会参考当地救助政策和具体灾情。',
            expanded: false
          },
          {
            question: '申报被驳回怎么办？',
            answer: '申报被驳回时，系统会显示驳回原因。您可以根据原因修改信息或补充材料后重新提交。如有疑问，可通过客服热线咨询。',
            expanded: false
          },
          {
            question: '救助资金如何发放？',
            answer: '救助资金会通过银行转账方式发放到您提供的银行账户。发放时间通常在审核通过后的7个工作日内。',
            expanded: false
          },
          {
            question: '同一灾害可以多次申报吗？',
            answer: '原则上，同一灾害只能申报一次。如灾情有变化或加重，可以提供新的证明材料进行补充申报。',
            expanded: false
          }
        ],
        feedbackForm: {
          content: '',
          contact: ''
        },
        feedbackList: [
          {
            id: 1,
            userName: '用户152****3698',
            time: '2023-08-15 14:23',
            content: '申报过程很简单明了，但希望能增加进度通知功能，这样就不用频繁查询了。',
            reply: '感谢您的建议，我们正在开发短信通知功能，预计下个版本上线。'
          },
          {
            id: 2,
            userName: '用户189****4567',
            time: '2023-08-10 09:15',
            content: '上传照片时偶尔会卡顿，希望能优化一下图片上传体验。',
            reply: '已收到反馈，技术团队正在优化图片压缩和上传功能，感谢您的耐心。'
          },
          {
            id: 3,
            userName: '用户135****7890',
            time: '2023-08-05 16:40',
            content: '能否增加更多的灾害类型选项？我遇到的情况不在现有选项中。',
            reply: '您好，我们将在下次更新中增加更多灾害类型选项，同时您也可以选择"其他灾害"并在描述中详细说明。'
          }
        ],
        hasMoreFeedback: true
      }
    },
    onLoad(options) {
      // 如果有传入标签页参数，则切换到对应标签页
      if (options.tab) {
        this.currentTab = parseInt(options.tab);
      }
      
      // 启动动画
      this._startLikeAnimation();
    },
    methods: {
      // 切换标签页
      switchTab(index) {
        this.currentTab = index;
      },
      
      // 切换指南章节展开/收起
      toggleSection(index) {
        this.guideData[index].expanded = !this.guideData[index].expanded;
      },
      
      // 切换FAQ展开/收起
      toggleFaq(index) {
        this.faqData[index].expanded = !this.faqData[index].expanded;
      },
      
      // 预览图片
      previewImage(url) {
        uni.previewImage({
          urls: [url],
          current: url
        });
      },
      
      // 提交留言
      submitFeedback() {
        if (!this.feedbackForm.content.trim()) {
          uni.showToast({
            title: '请输入留言内容',
            icon: 'none'
          });
          return;
        }
        
        // 显示加载中
        uni.showLoading({
          title: '提交中...'
        });
        
        // 模拟提交请求
        setTimeout(() => {
          uni.hideLoading();
          
          // 添加到留言列表（实际应该是提交到服务器）
          this.feedbackList.unshift({
            id: this.feedbackList.length + 1,
            userName: this.feedbackForm.contact ? '用户' + this.feedbackForm.contact.substring(0, 3) + '****' + this.feedbackForm.contact.substring(this.feedbackForm.contact.length - 4) : '匿名用户',
            time: this._formatDate(new Date()),
            content: this.feedbackForm.content,
            reply: ''
          });
          
          // 清空表单
          this.feedbackForm.content = '';
          this.feedbackForm.contact = '';
          
          uni.showToast({
            title: '留言提交成功',
            icon: 'success'
          });
        }, 1500);
      },
      
      // 加载更多留言
      loadMoreFeedback() {
        uni.showLoading({
          title: '加载中...'
        });
        
        // 模拟加载更多
        setTimeout(() => {
          uni.hideLoading();
          this.hasMoreFeedback = false;
          
          uni.showToast({
            title: '没有更多留言了',
            icon: 'none'
          });
        }, 1500);
      },
      
      // 拨打电话
      makePhoneCall() {
        uni.makePhoneCall({
          phoneNumber: '0123-4567890',
          fail: () => {
            uni.showToast({
              title: '拨号取消',
              icon: 'none'
            });
          }
        });
      },
      
      // 格式化日期
      _formatDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hour = date.getHours().toString().padStart(2, '0');
        const minute = date.getMinutes().toString().padStart(2, '0');
        
        return `${year}-${month}-${day} ${hour}:${minute}`;
      },
      
      // 启动动画
      _startLikeAnimation() {
        // 实现启动动画的逻辑
      }
    }
  }
</script>

<style lang="scss">
page {
  background-color: #f7f8fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
}

.help-container {
  padding-bottom: 120rpx;
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

/* 内容区域 */
.content-wrapper {
  padding: 30rpx;
}

/* 标签页 */
.tabs {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 30rpx;
  overflow: hidden;
}

.tab-item {
  flex: 1;
  height: 100rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
}

.tab-item.active {
  color: #4facfe;
  background-color: rgba(79, 172, 254, 0.1);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: #4facfe;
  border-radius: 2rpx;
}

.tab-icon {
  font-size: 40rpx;
  margin-bottom: 6rpx;
}

.tab-text {
  font-size: 26rpx;
}

/* 标签页内容 */
.tab-content {
  min-height: 600rpx;
}

/* 使用指南 */
.guide-section {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.section-header {
  padding: 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-icon {
  font-size: 32rpx;
  color: #999;
}

.section-content {
  padding: 20rpx 30rpx;
}

.guide-item {
  margin-bottom: 30rpx;
  border-bottom: 1px dashed #f0f0f0;
  padding-bottom: 20rpx;
}

.guide-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.guide-step {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.step-number {
  width: 50rpx;
  height: 50rpx;
  border-radius: 50%;
  background: #4facfe;
  color: #fff;
  font-size: 28rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20rpx;
}

.step-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.step-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.5;
  margin-left: 70rpx;
  margin-bottom: 20rpx;
}

.step-image {
    width: 100%;
  margin-left: 70rpx;
  border-radius: 12rpx;
  margin-top: 16rpx;
}

/* 常见问题 */
.faq-item {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.faq-question {
  padding: 30rpx;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.question-icon {
  font-size: 32rpx;
  color: #4facfe;
  margin-right: 20rpx;
}

.question-text {
  flex: 1;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.question-arrow {
  font-size: 32rpx;
  color: #999;
}

.faq-answer {
  padding: 30rpx;
  background: #f8f9fa;
}

.answer-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

/* 留言反馈 */
.feedback-form {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.form-header {
  margin-bottom: 20rpx;
}

.form-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.form-subtitle {
  font-size: 26rpx;
  color: #999;
}

.form-item {
  margin-bottom: 20rpx;
  position: relative;
}

.feedback-input {
    width: 100%;
  height: 240rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.input-count {
  position: absolute;
    right: 20rpx;
  bottom: 20rpx;
  font-size: 24rpx;
  color: #999;
}

.contact-input {
  width: 100%;
  height: 90rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.submit-btn {
    width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 45rpx;
  box-shadow: 0 6rpx 16rpx rgba(79, 172, 254, 0.3);
}

.feedback-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.list-header {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20rpx;
}

.list-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.list-count {
  font-size: 26rpx;
  color: #999;
  margin-left: 10rpx;
}

.empty-feedback {
  padding: 80rpx 0;
    text-align: center;
  }

.empty-icon {
  font-size: 120rpx;
  color: #ddd;
  margin-bottom: 20rpx;
  display: block;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.feedback-item {
  padding: 20rpx 0;
  border-bottom: 1px solid #f0f0f0;
}

.feedback-item:last-child {
  border-bottom: none;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.item-time {
  font-size: 24rpx;
  color: #999;
}

.item-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
  margin-bottom: 16rpx;
}

.reply-box {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-top: 16rpx;
}

.reply-label {
  font-size: 26rpx;
  color: #4facfe;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.reply-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
}

.load-more {
  text-align: center;
  padding: 30rpx 0 10rpx;
}

.more-text {
  font-size: 26rpx;
  color: #4facfe;
}

/* 底部联系方式 */
.contact-footer {
    position: fixed;
  bottom: 0;
  left: 0;
    right: 0;
  background: #fff;
  padding: 20rpx 0;
  text-align: center;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.contact-item {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10rpx;
}

.contact-item text {
  color: #4facfe;
  font-size: 28rpx;
}

.contact-item .cuIcon-service {
  margin-right: 10rpx;
  font-size: 32rpx;
}

.copyright {
  font-size: 24rpx;
  color: #999;
  }
</style>
