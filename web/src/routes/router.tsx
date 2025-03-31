import { createBrowserRouter } from 'react-router-dom'
import { preload } from 'swr'
import type { AxiosError } from 'axios'
import { MainLayout } from '../layouts/MainLayout.tsx'
import { SignInPage } from '../pages/SignInPage.tsx'
import { AdminLayout } from '../layouts/AdminLayout.tsx'
import { useUserStore } from '../stores/useUserStore.tsx'
import { getToken } from '../lib/token.ts'
import { http } from '../lib/Http.tsx'
import { ErrorUnauthorized } from '../errors.ts'
import { ErrorPage } from '../pages/ErrorPage.tsx'
import { ContestLayout } from '../layouts/ContestLayout.tsx'
import NotFoundPage from '../pages/404.tsx'
import AnnouncementPage from '../pages/AnnouncementPage.tsx'
import ContestListPage from '../pages/ContestListPage.tsx'
import HomePage from '../pages/HomePage.tsx'
import ProblemListPage from '../pages/ProblemListPage.tsx'
import ProblemPage from '../pages/ProblemPage.tsx'
import RecordListPage from '../pages/RecordListPage.tsx'
import RecordPage from '../pages/RecordPage.tsx'
import AdminAnnouncementEditPage from '../pages/admin/AdminAnnouncementEditPage.tsx'
import AdminAnnouncementPage from '../pages/admin/AdminAnnouncementPage.tsx'
import AdminContestEditPage from '../pages/admin/AdminContestEditPage.tsx'
import AdminContestPage from '../pages/admin/AdminContestPage.tsx'
import AdminHomePage from '../pages/admin/AdminHomePage.tsx'
import AdminProblemEditPage from '../pages/admin/AdminProblemEditPage.tsx'
import AdminProblemPage from '../pages/admin/AdminProblemPage.tsx'
import AdminUserEditPage from '../pages/admin/AdminUserEditPage.tsx'
import AdminUserImportPage from '../pages/admin/AdminUserImportPage.tsx'
import AdminUserPage from '../pages/admin/AdminUserPage.tsx'
import ContestClarificationDetailPage from '../pages/contest/ContestClarificationDetailPage.tsx'
import ContestClarificationListPage from '../pages/contest/ContestClarificationListPage.tsx'
import ContestHome from '../pages/contest/ContestHome.tsx'
import ContestNewClarificationPage from '../pages/contest/ContestNewClarificationPage.tsx'
import ContestProblem from '../pages/contest/ContestProblem.tsx'
import ContestProblemList from '../pages/contest/ContestProblemList.tsx'
import ContestStandingPage from '../pages/contest/ContestStandingPage.tsx'
import ContestSubmissionListPage from '../pages/contest/ContestSubmissionListPage.tsx'
import { ArticleListPage } from '../pages/ArticleListPage.tsx'
import { ArticleViewPage } from '../pages/ArticleViewPage.tsx'
import { ArticleEditPage } from '../pages/ArticleEditPage.tsx'
import { AdminArticlePage } from '../pages/admin/AdminArticlePage.tsx'
import { AdminArticleEditPage } from '../pages/admin/AdminArticleEditPage.tsx'
import { AdminGroupPage } from '../pages/admin/AdminGroupPage.tsx'
import { GroupListPage } from '../pages/GroupListPage.tsx'
import { GroupPage } from '../pages/GroupPage.tsx'
import { AdminHomeworkPage } from '../pages/admin/AdminHomeworkPage.tsx'
import { AdminHomeworkEditPage } from '../pages/admin/AdminHomeworkEditPage.tsx'
import { HomeworkPage } from '../pages/HomeworkPage.tsx'
import AdminRolePage from '../pages/admin/AdminRolePage.tsx'
import { AdminPermissionPage } from '../pages/admin/AdminPermissionPage.tsx'

async function rootLoader() {
  const user = useUserStore.getState().user
  if (user.id || !getToken()) {
    return true
  }
  return preload('/auth/current', async (path) => {
    return http.get<CurrentUser>(path)
      .then((res) => {
        useUserStore.setState({
          ...useUserStore.getState(),
          user: {
            ...res.data.data,
            iPermission: BigInt(res.data.data.permission || '0'),
          },
        })
        return true
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 401)
          throw new ErrorUnauthorized()
        throw err
      })
  })
}

export const router = createBrowserRouter(
  [
    {
      path: '/sign_in',
      element: <SignInPage />,
    },
    {
      path: '/admin',
      element: <AdminLayout />,
      errorElement: <ErrorPage />,
      loader: rootLoader,
      children: [
        { index: true, element: <AdminHomePage /> },
        {
          path: 'user',
          children: [
            { index: true, element: <AdminUserPage /> },
            { path: 'import', element: <AdminUserImportPage /> },
            { path: ':id/edit', element: <AdminUserEditPage /> },
          ],
        },
        {
          path: 'article',
          children: [
            { index: true, element: <AdminArticlePage /> },
            { path: ':id/edit', element: <AdminArticleEditPage /> },
          ],
        },
        {
          path: 'announcement',
          children: [
            { index: true, element: <AdminAnnouncementPage /> },
            { path: 'new', element: <AdminAnnouncementEditPage /> },
            { path: ':id/edit', element: <AdminAnnouncementEditPage /> },
          ],
        },
        {
          path: 'group',
          children: [
            { index: true, element: <AdminGroupPage /> },
          ],
        },
        {
          path: 'problem',
          children: [
            { index: true, element: <AdminProblemPage /> },
            { path: 'new', element: <AdminProblemEditPage /> },
            { path: ':id/edit', element: <AdminProblemEditPage /> },
          ],
        },
        {
          path: 'homework',
          children: [
            { index: true, element: <AdminHomeworkPage /> },
            { path: 'create', element: <AdminHomeworkEditPage /> },
            { path: ':id/edit', element: <AdminHomeworkEditPage /> },
          ],
        },
        {
          path: 'contest',
          children: [
            { index: true, element: <AdminContestPage /> },
            { path: 'new', element: <AdminContestEditPage /> },
            { path: ':id/edit', element: <AdminContestEditPage /> },
          ],
        },
        {
          path: 'role',
          children: [
            { index: true, element: <AdminRolePage /> },
          ],
        },
        {
          path: 'permission',
          children: [
            { index: true, element: <AdminPermissionPage /> },
          ],
        },
      ],
    },
    {
      path: '/',
      element: <MainLayout />,
      errorElement: <ErrorPage />,
      loader: rootLoader,
      children: [
        { index: true, element: <HomePage /> },
        {
          path: 'p',
          children: [
            { index: true, element: <ProblemListPage /> },
            { path: ':alias', element: <ProblemPage /> },
          ],
        },
        { path: 'a/:id', element: <AnnouncementPage /> },
        {
          path: 'r',
          children: [
            { index: true, element: <RecordListPage /> },
            { path: ':id', element: <RecordPage /> },
          ],
        },
        {
          path: 'c',
          children: [
            { index: true, element: <ContestListPage /> },
          ],
        },
        {
          path: 'article',
          children: [
            { index: true, element: <ArticleListPage /> },
            { path: 'new', element: <ArticleEditPage /> },
            {
              path: ':id',
              children: [
                { index: true, element: <ArticleViewPage /> },
                { path: 'edit', element: <ArticleEditPage /> },
              ],
            },
            { path: ':id', element: <ArticleViewPage /> },
          ],
        },
        {
          path: 'group',
          children: [
            { index: true, element: <GroupListPage /> },
            { path: ':id', element: <GroupPage /> },
          ],
        },
        {
          path: 'homework',
          children: [
            { path: ':id', element: <HomeworkPage /> },
          ],
        },
      ],
    },
    {
      path: '/c/:id',
      element: <ContestLayout />,
      errorElement: <ErrorPage />,
      loader: rootLoader,
      children: [
        { index: true, element: <ContestHome /> },
        {
          path: 'p',
          children: [
            { index: true, element: <ContestProblemList /> },
            { path: ':alias', element: <ContestProblem /> },
          ],
        },
        {
          path: 'clarification',
          children: [
            { index: true, element: <ContestClarificationListPage /> },
            { path: 'new', element: <ContestNewClarificationPage /> },
            { path: ':clarificationId', element: <ContestClarificationDetailPage /> },
          ],
        },
        {
          path: 'submission',
          children: [
            { index: true, element: <ContestSubmissionListPage /> },
          ],
        },
        {
          path: 'standing',
          element: <ContestStandingPage />,
        },
      ],
    },
    {
      path: '*',
      element: <NotFoundPage />,
    },
  ],
)
