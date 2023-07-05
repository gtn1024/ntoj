import React from 'react'
import { Layout, theme } from 'antd'
import { Outlet } from 'react-router-dom'
import { MenuComponent } from '../components/MenuComponent.tsx'
import { AccountComponent } from '../components/AccountComponent.tsx'
import { useInformationStore } from '../stores/useInformationStore.tsx'
import s from './MainLayout.module.scss'

const { Header, Content, Footer } = Layout
export const MainLayout: React.FC = () => {
  const { information } = useInformationStore()
  const {
    token: { colorBgContainer },
  } = theme.useToken()
  return (
    <Layout className={s.layout}>
      <Header className={s.header} style={{ background: colorBgContainer }}>
        <div className={s.logo}>
          NTOJ
        </div>
        <MenuComponent className={s.menu}/>
        <AccountComponent className={s.account}/>
      </Header>
      <Content className={s.content}>
        <Outlet/>
      </Content>
      <Footer className={s.footer}>
        <div>NTOJ ©2023</div>
        <div>
          {information.beian ? `备案号：${information.beian}` : null}
        </div>
      </Footer>
    </Layout>
  )
}
