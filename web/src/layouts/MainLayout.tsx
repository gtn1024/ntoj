import React from 'react'
import { Layout, theme } from 'antd'
import { Outlet } from 'react-router-dom'
import { MenuComponent } from '../components/MenuComponent.tsx'
import { AccountComponent } from '../components/AccountComponent.tsx'
import { useInformationStore } from '../stores/useInformationStore.tsx'

const { Header, Content, Footer } = Layout
export const MainLayout: React.FC = () => {
  const { information } = useInformationStore()
  const {
    token: { colorBgContainer },
  } = theme.useToken()
  return (
    <Layout>
      <Header className="flex" style={{ background: colorBgContainer }}>
        <div className="mr-1 flex items-center justify-center text-xl">
          <span>NTOJ</span>
        </div>
        <MenuComponent className="flex grow" />
        <AccountComponent className="flex justify-end" />
      </Header>
      <Content className="min-h-[calc(100vh-64px-80px)]">
        <Outlet />
      </Content>
      <Footer className="h-[80px] flex flex-col items-center justify-center bg-white text-center">
        <div>NTOJ ©2023</div>
        <div>
          {information.beian ? `备案号：${information.beian}` : null}
        </div>
      </Footer>
    </Layout>
  )
}
